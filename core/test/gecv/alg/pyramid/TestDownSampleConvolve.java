/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.alg.pyramid;

import gecv.alg.misc.ImageTestingOps;
import gecv.alg.filter.blur.BlurImageOps;
import gecv.alg.filter.convolve.KernelFactory;
import gecv.struct.convolve.Kernel1D_F32;
import gecv.struct.convolve.Kernel1D_I32;
import gecv.struct.image.ImageFloat32;
import gecv.struct.image.ImageSInt16;
import gecv.struct.image.ImageUInt8;
import gecv.testing.GecvTesting;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Abeles
 */
public class TestDownSampleConvolve {

	Random rand = new Random(234);
	int width = 20;
	int height = 30;
	int N = 2;

	/**
	 * See if images with an odd number of pixels are handled correctly
	 */
	@Test
	public void testOddSize_I8() {
		int width = 21;
		int height = 31;
		ImageUInt8 img = new ImageUInt8(width,height);
		ImageUInt8 downSampled = new ImageUInt8(width/N,height/N);

		Kernel1D_I32 kernel = KernelFactory.gaussian1D_I32(3);
		int storage[] = new int[ kernel.width ];
		DownSampleConvolve.downSample(kernel,img,downSampled, N,storage);

		// if it doesn't blow up it passed!  Yeah!
	}

	@Test
	public void testOddSize_I16() {
		int width = 21;
		int height = 31;
		ImageSInt16 img = new ImageSInt16(width,height);
		ImageSInt16 downSampled = new ImageSInt16(width/N,height/N);

		Kernel1D_I32 kernel = KernelFactory.gaussian1D_I32(3);
		int storage[] = new int[ kernel.width ];
		DownSampleConvolve.downSample(kernel,img,downSampled, N,storage);

		// if it doesn't blow up it passed!  Yeah!
	}

	@Test
	public void testOddSize_F32() {
		int width = 21;
		int height = 31;
		ImageFloat32 img = new ImageFloat32(width,height);
		ImageFloat32 downSampled = new ImageFloat32(width/N,height/N);

		Kernel1D_F32 kernel = KernelFactory.gaussian1D_F32(3,true);
		float storage[] = new float[ kernel.width ];
		DownSampleConvolve.downSample(kernel,img,downSampled, N,storage);

		// if it doesn't blow up it passed!  Yeah!
	}

	@Test
	public void downSample_I8() {
		ImageUInt8 img = new ImageUInt8(width,height);
		ImageTestingOps.randomize(img,rand, 0, 100);
		ImageUInt8 downSampled = new ImageUInt8(width/N,height/N);

		GecvTesting.checkSubImage(this,"downSampled_I8",true, img, downSampled);
	}

	public void downSampled_I8( ImageUInt8 img, ImageUInt8 downSampled) {
		ImageUInt8 convImg = new ImageUInt8(width,height);
		Kernel1D_I32 kernel = KernelFactory.gaussian1D_I32(3);

		BlurImageOps.kernel(img,convImg,kernel,new ImageUInt8(width,height));

		int storage[] = new int[ kernel.width ];
		DownSampleConvolve.downSample(kernel,img,downSampled, N,storage);

		for( int i = 0; i < height; i += N) {
			for( int j = 0; j < width; j += N) {
				int a = convImg.get(j,i);
				int b = downSampled.get(j/ N,i/ N);

				assertEquals("( "+j+" , "+i+" )",a,b);
			}
		}
	}

	@Test
	public void downSample_I16() {

		ImageSInt16 img = new ImageSInt16(width,height);
		ImageTestingOps.randomize(img,rand,0,200);
		ImageSInt16 downSampled = new ImageSInt16(width/N,height/N);

		GecvTesting.checkSubImage(this,"downSampled_I16",true, img, downSampled);
	}

	public void downSampled_I16( ImageSInt16 img, ImageSInt16 downSampled) {
		ImageSInt16 convImg = new ImageSInt16(width,height);
		Kernel1D_I32 kernel = KernelFactory.gaussian1D_I32(3);

		BlurImageOps.kernel(img,convImg,kernel,new ImageSInt16(width,height));

		int storage[] = new int[ kernel.width ];
		DownSampleConvolve.downSample(kernel,img,downSampled, N,storage);

		for( int i = 0; i < height; i += N) {
			for( int j = 0; j < width; j += N) {
				int a = convImg.get(j,i);
				int b = downSampled.get(j/ N,i/ N);

				assertEquals("( "+j+" , "+i+" )",a,b);
			}
		}
	}

@Test
	public void downSample_F32() {

		ImageFloat32 img = new ImageFloat32(width,height);
		ImageTestingOps.randomize(img,rand,0,200);
		ImageFloat32 downSampled = new ImageFloat32(width/N,height/N);

		GecvTesting.checkSubImage(this,"downSampled_F32",true, img, downSampled);
	}

	public void downSampled_F32( ImageFloat32 img, ImageFloat32 downSampled) {
		ImageFloat32 convImg = new ImageFloat32(width,height);
		Kernel1D_F32 kernel = KernelFactory.gaussian1D_F32(3,true);

		BlurImageOps.kernel(img,convImg,kernel,new ImageFloat32(width,height));

		float storage[] = new float[ kernel.width ];
		DownSampleConvolve.downSample(kernel,img,downSampled, N,storage);

		for( int i = 0; i < height; i += N) {
			for( int j = 0; j < width; j += N) {
				float a = convImg.get(j,i);
				float b = downSampled.get(j/ N,i/ N);

				assertEquals("( "+j+" , "+i+" )",a,b,1e-4);
			}
		}
	}
}
