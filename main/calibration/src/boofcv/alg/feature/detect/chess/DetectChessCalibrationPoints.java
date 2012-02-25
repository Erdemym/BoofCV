/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.alg.feature.detect.chess;

import boofcv.abst.feature.detect.extract.FeatureExtractor;
import boofcv.abst.feature.detect.extract.GeneralFeatureDetector;
import boofcv.abst.feature.detect.intensity.GeneralFeatureIntensity;
import boofcv.alg.feature.detect.grid.UtilCalibrationGrid;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.core.image.GeneralizedImageOps;
import boofcv.core.image.border.BorderType;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.detect.intensity.FactoryIntensityPoint;
import boofcv.numerics.solver.FitQuadratic3by3;
import boofcv.struct.ImageRectangle;
import boofcv.struct.QueueCorner;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;
import boofcv.struct.image.ImageUInt8;
import georegression.geometry.UtilPoint2D_I32;
import georegression.metric.Intersection2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I16;
import georegression.struct.point.Point2D_I32;
import georegression.struct.shapes.Polygon2D_F64;
import pja.sorting.QuickSort_F64;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static boofcv.alg.feature.detect.grid.AutoThresholdCalibrationGrid.selectNext;

/**
 * <p>
 * Detects calibration points inside a chessboard calibration target.  A crude approximation of the chess
 * board is found by thresholding the image dynamically.  Once found corner points are detected and pruned.
 * The remaining points are computed to sub-pixel accuracy by fitting a 2D quadratic to feature intensity
 * in a 3x3 region.
 * </p>
 *
 * <p>
 * The found control points are ensured to be returned in a row-major format with the correct number of rows and columns,
 * with a counter clockwise ordering.  Note that when viewed on the monitor this will appear to be clockwise because
 * the y-axis points down.
 *  </p>
 *
 * @author Peter Abeles
 */
public class DetectChessCalibrationPoints<T extends ImageSingleBand, D extends ImageSingleBand>
{
	// stores image derivative
	private D derivX;
	private D derivY;

	// number of control points 
	private int numColsPoints;
	private int numRowsPoints;

	// detects the chess board 
	private DetectChessSquaresBinary findBound;
	// binary images used to detect chess board
	private ImageUInt8 binary = new ImageUInt8(1,1);
	private ImageUInt8 eroded = new ImageUInt8(1,1);
	// number of attempts it will make to find the chessboard
	private int maxAttempts ;
	private double selectedThreshold;
	private List<Double> thresholdAttempts = new ArrayList<Double>();
	// maximum pixel intensity value
	private double maxPixelValue;
	
	// point detection algorithms
	private GeneralFeatureIntensity<T,D> intensityAlg;
	private GeneralFeatureDetector<T,D> detectorAlg;

	// point location at subpixel accuracy
	private List<Point2D_F64> subpixel;

	// number of points it expects to observer in the target
	private int expectedPoints;
	
	// used for sub-pixel refinement
	private FitQuadratic3by3 quadFit = new FitQuadratic3by3();

	/**
	 * Configures detection parameters
	 * 
	 * @param numCols Number of columns in square block grid.  Target dependent.
	 * @param numRows Number of rows in square block grid.  Target dependent.
	 * @param radius Side of interest point detection region.  Typically 5
	 * @param maxAttempts Maximum number of different threshold it will try to detect the image
	 * @param maxPixelValue Maximum pixel intensity value.  Almost always 255
	 * @param imageType Type of image being processed
	 */
	public DetectChessCalibrationPoints(int numCols, int numRows, int radius,
										int maxAttempts, double maxPixelValue,
										Class<T> imageType)
	{
		Class<D> derivType = GImageDerivativeOps.getDerivativeType(imageType);

		this.numColsPoints = 2*(numCols-1);
		this.numRowsPoints = 2*(numRows-1);
		
		this.maxAttempts = maxAttempts;
		this.maxPixelValue = maxPixelValue;

		expectedPoints = numColsPoints*numRowsPoints;

		derivX = GeneralizedImageOps.createSingleBand(derivType,1,1);
		derivY = GeneralizedImageOps.createSingleBand(derivType,1,1);

		intensityAlg = FactoryIntensityPoint.klt(radius,true,derivType);

		FeatureExtractor extractor = FactoryFeatureExtractor.nonmax(radius,20,radius,false,true);
		detectorAlg = new GeneralFeatureDetector<T, D>(intensityAlg,extractor,0);


		findBound = new DetectChessSquaresBinary(numCols, numRows, 20*4);
	}

	public boolean process( T gray ) {
		binary.reshape(gray.width,gray.height);
		eroded.reshape(gray.width, gray.height);

		// detect the chess board
		if( !detectChessBoard(gray) )
			return false;

		List<Point2D_I32> boundary = findBound.getBoundingQuad();

		// find image rectangle and detect features inside
		ImageRectangle rect = findImageRectangle(boundary);

		T subGray = (T)gray.subimage(rect.x0,rect.y0,rect.x1,rect.y1);
		derivX.reshape(subGray.width,subGray.height);
		derivY.reshape(subGray.width,subGray.height);

		// extended border to avoid false positives
		GImageDerivativeOps.sobel(subGray, derivX, derivY, BorderType.EXTENDED);

		// detect interest points
		detectorAlg.process(gray,derivX,derivY, null,null,null);

		QueueCorner corners = detectorAlg.getFeatures();

		// put points into original image coordinates
		List<Point2D_I32> points = convert(corners, rect.x0, rect.y0);

		// prune features not inside the bounding quadrilateral
		pruneOutside(points,boundary);

		// make sure enough points were detected
		if( points.size() < expectedPoints )
			return false;

		// select N brightest features
		points = selectBrightest(points, intensityAlg.getIntensity(),rect.x0,rect.y0);

		// put points into grid order
		List<Point2D_I32> predicted =
				ApproximateChessPoints.predictPoints(findBound.getCornerBlobs().get(0),
						numColsPoints, numRowsPoints);
		points = orderPoints(predicted, points);

		// compute pixels to sub-pixel accuracy
		subpixel = new ArrayList<Point2D_F64>();
		for( Point2D_I32 p : points)
			subpixel.add( refineSubpixel(p,rect.x0,rect.y0,intensityAlg.getIntensity()));

		UtilCalibrationGrid.enforceClockwiseOrder(subpixel, numColsPoints, numRowsPoints);

		return true;
	}

	/**
	 * Tests several different thresholds while attempting to detect a calibration grid in the image
	 */
	private boolean detectChessBoard( T gray ) {
		thresholdAttempts.clear();

		for( int i = 0; i < maxAttempts; i++ ) {
			selectedThreshold = selectNext(thresholdAttempts,maxPixelValue);

			GThresholdImageOps.threshold(gray, binary, selectedThreshold, true);

			// erode to make the squares separated
			BinaryImageOps.erode8(binary, eroded);

			if( findBound.process(eroded) )
				return true;
		}
		return false;
	}

	/**
	 * Computes a feature location to sub-pixel accuracy by fitting a 2D quadratic polynomial
	 * to corner intensities
	 *
	 * @param pt Point in image coordinates
	 * @param x0 intensity image x offset
	 * @param y0 intensity image y offset
	 * @param intensity Intensity image
	 * @return Sub-pixel point location
	 */
	private Point2D_F64 refineSubpixel( Point2D_I32 pt , 
										int x0 , int y0 , 
										ImageFloat32 intensity ) 
	{
		// sample feature intensity values in the local 3x3 region
		int index = 0;
		for( int i = -1; i <= 1; i++ ) {
			for( int j = -1; j <= 1; j++ , index++ ) {
				double value = intensity.get(pt.x-x0+j,pt.y-y0+i);

				quadFit.setValue(index,value);
			}
		}

		quadFit.process();
		
		double dx = quadFit.getDeltaX();
		double dy = quadFit.getDeltaY();

		if( Math.abs(dx) >= 1 || Math.abs(dy) >= 1 ) {
			// because it is an over determined system it can generate a solution
			// outside the bounds.  If that happens ignore the sub-pixel approximation
			return new Point2D_F64(pt.x,pt.y);
		} else {
			return new Point2D_F64(pt.x+quadFit.getDeltaX(),pt.y+quadFit.getDeltaY());
		}
	}

	/**
	 * Ensures tht the detected points are in correct grid order.  This is done by using the
	 * predicted point locations, which are already in order
	 *
	 * @param predicted Predicted and order points
	 * @param detected Detect corner location
	 * @return Ordered detected points
	 */
	private List<Point2D_I32> orderPoints( List<Point2D_I32> predicted , List<Point2D_I32> detected )
	{
		List<Point2D_I32> ret = new ArrayList<Point2D_I32>();
		
		for( Point2D_I32 p : predicted ) {
			double bestDist = Double.MAX_VALUE;
			Point2D_I32 best = null;
			
			for( Point2D_I32 d : detected ) {
				double dist = UtilPoint2D_I32.distance(p,d);
			
				if( dist < bestDist ) {
					bestDist = dist;
					best = d;
				}
			}
			
			ret.add(best);
		}

		return ret;
	}

	/**
	 * Finds an axis aligned rectangle that contains the bounding quadrilateral
	 */
	private ImageRectangle findImageRectangle( List<Point2D_I32> quad )
	{
		int x0,y0,x1,y1;
		
		Point2D_I32 p = quad.get(0);
		x0=x1=p.x;
		y0=y1=p.y;
		
		for( int i = 1; i < 4; i++ ) {
			p = quad.get(i);
			if( p.x < x0 )
				x0 = p.x;
			else if( p.x > x1 )
				x1 = p.x;

			if( p.y < y0 )
				y0 = p.y;
			else if( p.y > y1 )
				y1 = p.y;
		}
		
		return new ImageRectangle(x0,y0,x1,y1);
	}

	/**
	 * Prunes detected corners not inside the image
	 */
	private void pruneOutside( List<Point2D_I32> corners , List<Point2D_I32> quad ) {
		Polygon2D_F64 poly = new Polygon2D_F64(4);
		for( int i = 0; i < 4; i++ ) {
			Point2D_I32 p = quad.get(i);
			poly.vertexes[i].set(p.x,p.y);
		}

		Iterator<Point2D_I32> iter = corners.iterator();

		Point2D_F64 a = new Point2D_F64();
		while( iter.hasNext() ) {
			Point2D_I32 p = iter.next();
			a.set(p.x,p.y);

			if( !Intersection2D_F64.containConvex(poly,a) )
				iter.remove();
		}
	}

	/**
	 * Converts from a corner in the sub-image into a point in the full image
	 */
	private List<Point2D_I32> convert(QueueCorner found , int x0 , int y0 ) {
		List<Point2D_I32> points = new ArrayList<Point2D_I32>();
		for( int i = 0; i < found.size(); i++ ) {
			Point2D_I16 c = found.get(i);
			points.add(new Point2D_I32(c.x+x0, c.y+y0));
		}
		return points;
	}

	/**
	 * Out of the remaining points, just select the brightest to remove any remaining false positives
	 */
	private List<Point2D_I32> selectBrightest( List<Point2D_I32> points , ImageFloat32 intensity ,
											   int offX , int offY ) {
		if( points.size() == expectedPoints )
			return points;


		double values[] = new double[points.size()];
		int indexes[] = new int[points.size()];

		for( int i = 0; i < points.size(); i++ ) {
			Point2D_I32 p = points.get(i);

			values[i] = -intensity.get(p.x-offX, p.y-offY);
		}

		new QuickSort_F64().sort(values,points.size(),indexes);

		List<Point2D_I32> ret = new ArrayList<Point2D_I32>();

		for( int i = 0; i < expectedPoints; i++ ) {
			ret.add(points.get(indexes[i]));
		}

		return ret;
	}

	public DetectChessSquaresBinary getFindBound() {
		return findBound;
	}

	public List<Point2D_F64> getPoints() {
		return subpixel;
	}

	public ImageUInt8 getBinary() {
		return eroded;
	}

	public double getSelectedThreshold() {
		return selectedThreshold;
	}
}