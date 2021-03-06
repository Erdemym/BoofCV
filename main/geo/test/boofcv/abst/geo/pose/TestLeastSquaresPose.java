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

package boofcv.abst.geo.pose;

import boofcv.alg.geo.pose.CommonMotionNPoint;
import boofcv.alg.geo.pose.PoseRodriguesCodec;
import boofcv.struct.geo.GeoModelRefine;
import boofcv.struct.geo.PointPosePair;
import georegression.geometry.RotationMatrixGenerator;
import georegression.struct.se.Se3_F64;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Peter Abeles
 */
public class TestLeastSquaresPose extends CommonMotionNPoint {

	Se3_F64 found = new Se3_F64();

	@Test
	public void perfect() {

		Se3_F64 motion = new Se3_F64();
		motion.getR().set(RotationMatrixGenerator.eulerArbitrary(0, 1, 2, 0.05, -0.03, 0.02));
		motion.getT().set(0.1,-0.1,0.01);

		generateScene(10,motion,false);

		GeoModelRefine<Se3_F64,PointPosePair> alg = new LeastSquaresPose(1e-8,200,new PoseRodriguesCodec());
		
		assertTrue(alg.process(motion, pointPose, found));

		assertEquals(motion.getT().getX(),found.getX(),1e-8);
		assertEquals(motion.getT().getY(),found.getY(),1e-8);
		assertEquals(motion.getT().getZ(),found.getZ(),1e-8);
	}

	@Test
	public void noisy() {

		Se3_F64 motion = new Se3_F64();
		motion.getR().set(RotationMatrixGenerator.eulerArbitrary(0, 1, 2, 0.05, -0.03, 0.02));
		motion.getT().set(0.1,-0.1,0.01);

		generateScene(50,motion,false);

		LeastSquaresPose alg = new LeastSquaresPose(1e-20,500,new PoseRodriguesCodec());

		Se3_F64 n = motion.copy();
		n.getT().setX(0);
		
		assertTrue(alg.process(n, pointPose,found));

		assertEquals(motion.getT().getX(),found.getX(),1e-5);
		assertEquals(motion.getT().getY(),found.getY(),1e-5);
		assertEquals(motion.getT().getZ(),found.getZ(),1e-5);
	}

}
