/*
 * Copyright (c) 2011-2012, Peter Abeles. All Rights Reserved.
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

package boofcv.alg.feature.detect.extract;

import boofcv.alg.misc.ImageTestingOps;
import boofcv.struct.QueueCorner;
import boofcv.struct.image.ImageFloat32;
import georegression.struct.point.Point2D_I16;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
public class TestFastNonMaxExtractor extends GenericNonMaxTests {
	Random rand = new Random(0x334);

	@Test
	public void standardTests() {
		super.allStandard(true);
		super.allStandard(false);
	}

	/**
	 * Checks to see if {@link FastNonMaxExtractor} produces exactly the same results as
	 * {@link NonMaxExtractorNaive}
	 */
	@Test
	public void compareToNaive() {
		compareToNaive(true);
		compareToNaive(false);
	}

	public void compareToNaive( boolean useStrict ) {
		ImageFloat32 inten = new ImageFloat32(30, 40);

		QueueCorner fastCorners = new QueueCorner(inten.getWidth() * inten.getHeight());
		QueueCorner regCorners = new QueueCorner(inten.getWidth() * inten.getHeight());

		for (int useSubImage = 0; useSubImage < 2; useSubImage++) {
			// make sure it handles sub images correctly
			if (useSubImage == 1) {
				ImageFloat32 larger = new ImageFloat32(inten.width + 10, inten.height + 8);
				inten = larger.subimage(0, 0, 30, 40);
			}

			for (int nonMaxWidth = 3; nonMaxWidth <= 9; nonMaxWidth += 2) {
				FastNonMaxExtractor fast = new FastNonMaxExtractor(nonMaxWidth / 2, 0.6F, useStrict);
				NonMaxExtractorNaive reg = new NonMaxExtractorNaive(nonMaxWidth / 2, 0.6F, useStrict);

				for (int i = 0; i < 10; i++) {
					ImageTestingOps.randomize(inten, rand, 0, 10);

					fast.process(inten, fastCorners);
					reg.process(inten, regCorners);

					assertTrue(fastCorners.size() > 0);

					assertEquals(regCorners.size(), fastCorners.size());

					for (int j = 0; j < regCorners.size(); j++) {
						Point2D_I16 a = fastCorners.get(j);
						Point2D_I16 b = regCorners.get(j);

						assertEquals(b.getX(), a.getX());
						assertEquals(b.getY(), a.getY());
					}
				}
			}
		}
	}

	@Override
	public void findLocalMaximums(ImageFloat32 intensity, float threshold, int radius,
								  boolean useStrict, QueueCorner found) {
		FastNonMaxExtractor fast = new FastNonMaxExtractor(radius, threshold,useStrict);

		fast.process(intensity,found);
	}
}