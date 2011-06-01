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

package gecv.alg.filter.blur.impl;

import gecv.alg.filter.blur.MedianImageFilter;
import gecv.struct.image.ImageUInt8;

/**
 * <p>
 * A faster version of the histogram median filter that only processes the inner portion of the image.  Instead of
 * rebuilding the histogram from scratch for each pixel the histogram is updated using results from the previous pixel.  
 * </p>
 *
 * <p>
 * Based on the description in some papers I believe this algorithm is similar to the one proposed in:<br>
 * Huang, T.S., Yang, G.J. and Tang, G.Y. (1979) A fast two-dimensional median filtering algorithm. IEEE Trans.
 * Acoust. Speech Signal Process. 27, 13-18
 * </p>
 * @author Peter Abeles
 */
public class MedianHistogramInner_I8 implements MedianImageFilter<ImageUInt8> {

	int radius;
	int histogram[] = new int[ 256 ];
	int offset[];
	int threshold;

	public MedianHistogramInner_I8(int radius) {
		this.radius = radius;

		int w = radius*2+1;
		offset = new int[w*w];
		threshold = offset.length/2+1;
	}

	@Override
	public int getRadius() {
		return radius;
	}

	@Override
	public void process(ImageUInt8 input, ImageUInt8 output) {
		initialize(input);

		int boxWidth = radius*2+1;

		for( int y = radius; y < input.height-radius; y++ ) {
			int seed = input.startIndex + y*input.stride+radius;

			zeroHistogram();
			// compute the median value for the first x component and initialize the system
			for( int i = 0; i < offset.length; i++ ) {
				int val = input.data[seed+offset[i]] & 0xFF;
//					System.out.println(val);
				histogram[val]++;
			}

			int count = 0;
			int median;
			for( median = 0; median < 256; median++ ) {
				count += histogram[median];
				if( count >= threshold )
					break;
			}
			output.data[ output.startIndex+y*output.stride+radius] = (byte)median;

			// remove the left most pixel from the histogram
			for( int i = 0; i < offset.length; i += boxWidth ) {
				int val = input.data[seed+offset[i]] & 0xFF;
				histogram[val]--;
			}

			for( int x = radius+1; x < input.width-radius; x++ ) {
				seed = input.startIndex + y*input.stride+x;

				// add the right most pixels to the histogram
				for( int i = boxWidth-1; i < offset.length; i += boxWidth ) {
					int val = input.data[seed+offset[i]] & 0xFF;
					histogram[val]++;
				}

				// find the median
				count = 0;
				for( median = 0; median < 256; median++ ) {
					count += histogram[median];
					if( count >= threshold )
						break;
				}
				output.data[ output.startIndex+y*output.stride+x] = (byte)median;

				// remove the left most pixels from the histogram
				for( int i = 0; i < offset.length; i += boxWidth ) {
					int val = input.data[seed+offset[i]] & 0xFF;
					histogram[val]--;
				}
			}
		}
	}

	private void initialize(ImageUInt8 input) {
		int index = 0;
		for( int i = -radius; i <= radius; i++ ) {
			for( int j = -radius; j <= radius; j++ ) {
				offset[index++] = i*input.stride + j;
			}
		}
	}

	private void zeroHistogram() {
		for( int i =0; i < histogram.length; i++ ) {
			histogram[i] = 0;
		}
	}

	private void printHistogram() {
		for( int i = 0; i < histogram.length; i++ ) {
			if( histogram[i] != 0 ) {
				System.out.printf("[%d] = %d\n",i,histogram[i]);
			}
		}
	}
}
