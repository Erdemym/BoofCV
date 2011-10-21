/*
 * Copyright (c) 2011, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://www.boofcv.org).
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

package boofcv.alg.feature.orientation.impl;

import boofcv.alg.feature.describe.SurfDescribeOps;
import boofcv.alg.feature.orientation.OrientationAverageIntegral;
import boofcv.misc.BoofMiscOps;
import boofcv.struct.image.ImageFloat32;
import georegression.metric.UtilAngle;


/**
 * <p>
 * Implementation of {@link boofcv.alg.feature.orientation.OrientationAverageIntegral} for a specific type.
 * </p>
 *
 * @author Peter Abeles
 */
public class ImplOrientationSlidingWindowIntegral_F32
		extends OrientationAverageIntegral<ImageFloat32>
{
	// where the output from the derivative is stored
	float[] derivX;
	float[] derivY;

	// derivative needed for border algorithm
	double[] borderDerivX;
	double[] borderDerivY;

	// number of different angles it will consider
	protected int numAngles;
	// the size of the window it will consider
	protected double windowSize;
	// the angle each pixel is pointing
	protected double angles[];

	/**
	 *
	 * @param radius Radius of the region being considered in terms of Wavelet samples. Typically 6.
	 * @param weighted If edge intensities are weighted using a Gaussian kernel.
	 */
	public ImplOrientationSlidingWindowIntegral_F32(int numAngles , double windowSize,
													int radius, boolean weighted ) {
		super(radius,weighted);
		this.numAngles = numAngles;
		this.windowSize = windowSize;

		derivX = new float[width*width];
		derivY = new float[width*width];

		borderDerivX = new double[width*width];
		borderDerivY = new double[width*width];

		angles = new double[ width*width ];
	}

	@Override
	public double compute(int c_x, int c_y ) {
		// use a faster algorithm if it is entirely inside
//		if( SurfDescribeOps.isInside(ii,c_x,c_y,radius,4,scale))  {
//			SurfDescribeOps.gradient_noborder(ii,c_x,c_y,radius,4,scale,derivX,derivY);
//		} else {
			SurfDescribeOps.gradient(ii,c_x,c_y,radius,4,scale, true, borderDerivX,borderDerivY);
			BoofMiscOps.convertTo_F32(borderDerivX,derivX);
			BoofMiscOps.convertTo_F32(borderDerivY,derivY);
//		}

		for( int i = 0; i < derivX.length; i++ ) {
			angles[i] = Math.atan2(derivY[i],derivX[i]);
		}
//
		if( weights == null ) {
			return unweighted();
		} else {
			return weighted();
		}
	}

	private double unweighted() {
		double windowRadius = windowSize/2.0;
		double bestScore = -1;
		double bestAngle = 0;
		double stepAngle = Math.PI*2.0/numAngles;

		for( double angle = -Math.PI; angle < Math.PI; angle += stepAngle ) {
			double dx = 0;
			double dy = 0;
			for( int i = 0; i < angles.length; i++ ) {
				int x = i%width;
				int y = i/width;
				if( x*x + y*y >= 36 )
					continue;

				double diff = UtilAngle.dist(angle, angles[i]);
				if( diff <= windowRadius) {
					dx += derivX[i];
					dy += derivY[i];
				}
			}
			double n = dx*dx + dy*dy;
			if( n > bestScore) {
				bestAngle = Math.atan2(dy,dx);
				bestScore = n;
			}
		}

		return bestAngle;
	}

	private double weighted() {
		double windowRadius = windowSize/2.0;
		double bestScore = -1;
		double bestAngle = 0;
		double stepAngle = Math.PI*2.0/numAngles;

		for( double angle = -Math.PI; angle < Math.PI; angle += stepAngle ) {
			double dx = 0;
			double dy = 0;
			for( int i = 0; i < angles.length; i++ ) {
				// todo hack, change
				int x = i%width-radius;
				int y = i/width-radius;
				if( x*x + y*y >= 36 )
					continue;

				double diff = UtilAngle.dist(angle, angles[i]);
				if( diff <= windowRadius) {
					dx += weights.data[i]*derivX[i];
					dy += weights.data[i]*derivY[i];
				}
			}
			double n = dx*dx + dy*dy;
			if( n > bestScore) {
				bestAngle = Math.atan2(dy,dx);
				bestScore = n;
			}
		}

		return bestAngle;
	}


	@Override
	public Class<ImageFloat32> getImageType() {
		return ImageFloat32.class;
	}
}