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

package boofcv.abst.feature.detect.interest;

import boofcv.alg.feature.orientation.OrientationImage;
import boofcv.struct.image.ImageSingleBand;
import georegression.struct.point.Point2D_F64;

/**
 * Provides the capability to tack on a different algorithm for the feature's location, scale, and orientation.
 *
 * @author Peter Abeles
 */
public class InterestPointDetectorOverride< T extends ImageSingleBand>
		implements InterestPointDetector<T>
{
	InterestPointDetector<T> detector;
	OrientationImage<T> orientation;

	/**
	 * Specifies which algorithms are to be used.  If orientation is specified then it will override the orientation
	 * provided by 'detector'
	 *
	 * @param detector Interest point detector and default scale and orientation.
	 * @param orientation If not null then this will be used to estimate the feature's orientation.
	 */
	public InterestPointDetectorOverride(InterestPointDetector<T> detector, OrientationImage<T> orientation) {
		this.detector = detector;
		this.orientation = orientation;
	}

	@Override
	public void detect(T input) {
		detector.detect(input);
		if( orientation != null )
			orientation.setImage(input);
	}

	@Override
	public int getNumberOfFeatures() {
		return detector.getNumberOfFeatures();
	}

	@Override
	public Point2D_F64 getLocation(int featureIndex) {
		return detector.getLocation(featureIndex);
	}

	@Override
	public double getScale(int featureIndex) {
		return detector.getScale(featureIndex);
	}

	@Override
	public double getOrientation(int featureIndex) {
		if( orientation == null )
			return detector.getOrientation(featureIndex);

		Point2D_F64 p = detector.getLocation(featureIndex);
		orientation.setScale(getScale(featureIndex));
		return orientation.compute(p.x,p.y);
	}

	@Override
	public double getCanonicalRadius() {
		return detector.getCanonicalRadius();
	}

	@Override
	public boolean hasScale() {
		return detector.hasScale();
	}

	@Override
	public boolean hasOrientation() {
		if( orientation == null )
			return detector.hasOrientation();
		return true;
	}
}
