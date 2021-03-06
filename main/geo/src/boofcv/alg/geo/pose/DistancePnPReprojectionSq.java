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

package boofcv.alg.geo.pose;

import boofcv.numerics.fitting.modelset.DistanceFromModel;
import boofcv.struct.geo.PointPosePair;
import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point3D_F64;
import georegression.struct.se.Se3_F64;
import georegression.transform.se.SePointOps_F64;

import java.util.List;

/**
 * <p>
 * Computes the reprojection error squared for a given motion and {@link PointPosePair}.  If the intrinsic
 * parameters are provided then the error will be computed in pixels.   Observations are assumed to be
 * in normalized image coordinates.
 * <center>error = (x'-x)^2 + (y' - y)^2</center>
 * where (x,y) is the observed point location and (x',y') is the reprojected point from the 3D coordinate and coordinate
 * transformation.
 * </p>
 *
 * @author Peter Abeles
 */
public class DistancePnPReprojectionSq implements DistanceFromModel<Se3_F64,PointPosePair> {

	// transform from world to camera
	private Se3_F64 worldToCamera;

	// storage for point in camera frame
	private Point3D_F64 X = new Point3D_F64();

	// ------- intrinsic camera parameters from calibration matrix
	private double fx; // focal length x
	private double fy; // focal length y
	private double skew; // pixel skew

	/**
	 * Computes reprojection error in units of normalized image coordinates
	 */
	public DistancePnPReprojectionSq() {
		this(1,1,0);
	}

	/**
	 * Computes reprojection error in pixels when provided to camera's intrinsic calibration.
	 *
	 * @param fx focal length x
	 * @param fy focal length y
	 * @param skew pixel skew
	 */
	public DistancePnPReprojectionSq(double fx, double fy, double skew) {
		this.fx = fx;
		this.fy = fy;
		this.skew = skew;
	}

	@Override
	public void setModel(Se3_F64 worldToCamera) {
		this.worldToCamera = worldToCamera;
	}

	@Override
	public double computeDistance(PointPosePair pt) {
		// compute point location in camera frame
		SePointOps_F64.transform(worldToCamera,pt.location,X);

		Point2D_F64 p = pt.getObserved();

		double dy = X.y/X.z - p.y;
		double dx = (X.x/X.z - p.x)*fx + dy*skew;
		dy *= fy;

		return dx*dx + dy*dy;
	}

	@Override
	public void computeDistance(List<PointPosePair> obserations, double[] distance) {
		for( int i = 0; i < obserations.size(); i++ )
			distance[i] = computeDistance(obserations.get(i));
	}
}
