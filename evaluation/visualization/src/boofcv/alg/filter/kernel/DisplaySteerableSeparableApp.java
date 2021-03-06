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

package boofcv.alg.filter.kernel;

import boofcv.factory.filter.kernel.FactorySteerable;
import boofcv.struct.convolve.Kernel2D;
import boofcv.struct.convolve.Kernel2D_F32;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;


/**
 * @author Peter Abeles
 */
public class DisplaySteerableSeparableApp<T extends ImageSingleBand, K extends Kernel2D>
	extends DisplaySteerableBase<T,K>
{
	public DisplaySteerableSeparableApp(Class<T> imageType, Class<K> kernelType) {
		super(imageType, kernelType);

//		double v;
//		v = FactorySteerCoefficients.separable(1).compute(0,1);
//		v = FactorySteerCoefficients.separable(2).compute(0,1);
//		v = FactorySteerCoefficients.separable(3).compute(0,1);
//		v = FactorySteerCoefficients.separable(3).compute(0,2);
//		v = FactorySteerCoefficients.separable(4).compute(0,1);
//		v = FactorySteerCoefficients.separable(4).compute(0,2);
//		v = FactorySteerCoefficients.separable(4).compute(0,3);

	}

	@Override
	protected SteerableKernel<K> createKernel(int orderX, int orderY) {
		return FactorySteerable.separable(kernelType,orderX,orderY,radius);
	}

	public static void main( String args[] ) {
		DisplaySteerableSeparableApp<ImageFloat32,Kernel2D_F32> app =
				new DisplaySteerableSeparableApp<ImageFloat32,Kernel2D_F32>(ImageFloat32.class,Kernel2D_F32.class);

//		DisplaySteerableSeparableApp<ImageSInt32, Kernel2D_I32> app =
//				new DisplaySteerableSeparableApp<ImageSInt32,Kernel2D_I32>(ImageSInt32.class,Kernel2D_I32.class);
//		app.process();
	}
}
