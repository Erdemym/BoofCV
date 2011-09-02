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

package boofcv.alg.filter.derivative.impl;

import boofcv.alg.filter.derivative.CompareDerivativeToConvolution;
import boofcv.alg.filter.derivative.GradientThree;
import boofcv.alg.misc.ImageTestingOps;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;
import org.junit.Test;

import java.util.Random;

/**
 * @author Peter Abeles
 */
public class TestGradientThree_Standard {

	Random rand = new Random(234);

	int width = 20;
	int height = 25;

	@Test
	public void compareToConvolve_I8() throws NoSuchMethodException {
		CompareDerivativeToConvolution validator = new CompareDerivativeToConvolution();
		validator.setTarget(GradientThree_Standard.class.getMethod("process",
				ImageUInt8.class, ImageSInt16.class, ImageSInt16.class ));

		validator.setKernel(0, GradientThree.kernelDeriv_I32,true);
		validator.setKernel(1, GradientThree.kernelDeriv_I32,false);

		ImageUInt8 input = new ImageUInt8(width,height);
		ImageTestingOps.randomize(input, rand, 0, 10);
		ImageSInt16 derivX = new ImageSInt16(width,height);
		ImageSInt16 derivY = new ImageSInt16(width,height);

		validator.compare(false,input,derivX,derivY);
	}

	@Test
	public void compareToConvolve_F32() throws NoSuchMethodException {
		CompareDerivativeToConvolution validator = new CompareDerivativeToConvolution();
		validator.setTarget(GradientThree_Standard.class.getMethod("process",
				ImageFloat32.class, ImageFloat32.class, ImageFloat32.class ));

		validator.setKernel(0, GradientThree.kernelDeriv_F32,true);
		validator.setKernel(1, GradientThree.kernelDeriv_F32,false);

		ImageFloat32 input = new ImageFloat32(width,height);
		ImageTestingOps.randomize(input, rand, 0, 10);
		ImageFloat32 derivX = new ImageFloat32(width,height);
		ImageFloat32 derivY = new ImageFloat32(width,height);

		validator.compare(false,input,derivX,derivY);
	}
}
