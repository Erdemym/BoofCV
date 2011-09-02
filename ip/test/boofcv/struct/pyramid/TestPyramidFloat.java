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

package boofcv.struct.pyramid;

import boofcv.struct.image.ImageUInt8;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Peter Abeles
 */
public class TestPyramidFloat {

	int width = 80;
	int height = 160;

	@Test
	public void setScaling() {
		// see if all the layers are set correctly
		PyramidFloat<ImageUInt8> pyramid = new PyramidFloat<ImageUInt8>(ImageUInt8.class);

		pyramid.setScaleFactors(1,2,5.5);
		pyramid.initialize(width,height);
		assertEquals(width , pyramid.getLayer(0).width);
		assertEquals(height , pyramid.getLayer(0).height);

		assertEquals(width / 2, pyramid.getLayer(1).width);
		assertEquals(height / 2, pyramid.getLayer(1).height);

		assertEquals((int)Math.ceil(width / 5.5), pyramid.getLayer(2).width);
		assertEquals((int)Math.ceil(height / 5.5), pyramid.getLayer(2).height);

		// try it with a scaling not equal to 1
		pyramid.setScaleFactors(2,4);
		pyramid.initialize(width,height);

		assertEquals(width / 2, pyramid.getLayer(0).width);
		assertEquals(height / 2, pyramid.getLayer(0).height);
		assertEquals(width / 4, pyramid.getLayer(1).width);
		assertEquals(height / 4, pyramid.getLayer(1).height);
	}
}
