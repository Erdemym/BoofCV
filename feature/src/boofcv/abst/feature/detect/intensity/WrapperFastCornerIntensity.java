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

package boofcv.abst.feature.detect.intensity;

import boofcv.alg.feature.detect.intensity.FastCornerIntensity;
import boofcv.struct.QueueCorner;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageFloat32;

/**
 * Wrapper around children of {@link boofcv.alg.feature.detect.intensity.FastCornerIntensity}.
 * 
 * @author Peter Abeles
 */
public class WrapperFastCornerIntensity<I extends ImageBase, D extends ImageBase> implements GeneralFeatureIntensity<I,D> {

	FastCornerIntensity<I> alg;

	public WrapperFastCornerIntensity(FastCornerIntensity<I> alg) {
		this.alg = alg;
	}

	@Override
	public void process(I input, D derivX , D derivY , D derivXX , D derivYY , D derivXY ) {
		alg.process(input);
	}

	@Override
	public ImageFloat32 getIntensity() {
		return alg.getIntensity();
	}

	@Override
	public QueueCorner getCandidates() {
		return alg.getCandidates();
	}

	@Override
	public boolean getRequiresGradient() {
		return false;
	}

	@Override
	public boolean getRequiresHessian() {
		return false;
	}

	@Override
	public boolean hasCandidates() {
		return true;
	}
}
