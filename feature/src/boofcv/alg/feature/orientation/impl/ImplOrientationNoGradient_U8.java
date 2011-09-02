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

import boofcv.alg.feature.orientation.OrientationNoGradient;
import boofcv.struct.image.ImageUInt8;


/**
 *
 * <p>
 * Implementation of {@link boofcv.alg.feature.orientation.OrientationNoGradient} for a specific image type.
 * </p>
 *
 * <p>
 * WARNING: Do not modify.  Automatically generated by {@link GenerateImplOrientationNoGradient}.
 * </p>
 *
 * @author Peter Abeles
 */
public class ImplOrientationNoGradient_U8 extends OrientationNoGradient<ImageUInt8> {

	public ImplOrientationNoGradient_U8(int radius) {
		super(radius);
	}


	@Override
	public double computeAngle( int c_x , int c_y ) {

		float sumX=0,sumY=0;

		for( int y = rect.y0; y < rect.y1; y++ ) {
			int index = image.startIndex + image.stride*y + rect.x0;
			int indexW = (y-c_y+radiusScale)*kerCosine.width + rect.x0-c_x+radiusScale;

			for( int x = rect.x0; x < rect.x1; x++ , index++ , indexW++ ) {
				int val = image.data[index]& 0xFF;
				sumX += kerCosine.data[indexW]*val;
				sumY += kerSine.data[indexW]*val;
			}
		}

		return Math.atan2(sumY,sumX);
	}

	public Class<ImageUInt8> getImageType() {
		return ImageUInt8.class;
	}

}
