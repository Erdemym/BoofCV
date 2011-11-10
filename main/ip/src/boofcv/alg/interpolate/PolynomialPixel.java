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

package boofcv.alg.interpolate;


import boofcv.alg.interpolate.array.PolynomialNevilleFixed_F32;
import boofcv.struct.image.ImageBase;

/**
 * <p>
 * Polynomial interpolation using {@link PolynomialNevilleFixed_F32 Neville's} algorithm.
 * First interpolation is performed along the horizontal axis, centered at the specified x-coordinate.
 * Then a second pass is done along the vertical axis using the output from the first pass.
 * </p>
 *
 * <p>
 * The code is unoptimized and the algorithm is relatively expensive.
 * </p>
 *
 * @author Peter Abeles
 */
public abstract class PolynomialPixel<T extends ImageBase> implements InterpolatePixel<T> {
	// the image that is being interpolated
	protected T image;

	protected int M;
	// if even need to add one to initial coordinate to make sure
	// the point interpolated is bounded inside the interpolation points
	protected int offM;

	// temporary arrays used in the interpolation
	protected float horiz[];
	protected float vert[];

	// the minimum and maximum pixel intensity values allowed
	protected float min;
	protected float max;

	protected PolynomialNevilleFixed_F32 interp1D;

	public PolynomialPixel(int maxDegree, float min, float max) {
		this.M = maxDegree;
		this.min = min;
		this.max = max;
		horiz = new float[maxDegree];
		vert = new float[maxDegree];

		if( maxDegree % 2 == 0 ) {
			offM = 1;
		} else {
			offM = 0;
		}

		interp1D = new PolynomialNevilleFixed_F32(maxDegree);
	}

	@Override
	public void setImage(T image) {
		this.image = image;
	}

	@Override
	public T getImage() {
		return image;
	}

	@Override
	public boolean isInSafeBounds(float x, float y) {
		int x0 = (int)x - M/2 + offM;
		int x1 = x0 + M;
		int y0 = (int)y - M/2 + offM;
		int y1 = y0 + M;

		return (x0 >= 0 && y0 >= 0 && x1 < image.width && y1 <image.height);
	}
}
