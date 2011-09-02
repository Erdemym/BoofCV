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

package boofcv.alg.feature.detect.edge.impl;

import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageSInt32;

/**
 * <p>
 * Implementations of the core algorithms of {@link boofcv.alg.feature.detect.edge.GradientToEdgeFeatures}.
 * </p>
 *
 * <p>
 * WARNING: Do not modify.  Automatically generated by {@link GenerateImplGradientToEdgeFeatures}.
 * </p>
 * 
 * @author Peter Abeles
 */
public class ImplGradientToEdgeFeatures {

	static public void intensityE( ImageFloat32 derivX , ImageFloat32 derivY , ImageFloat32 intensity )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexI = intensity.startIndex + y*intensity.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexI++ ) {
				float dx = derivX.data[indexX];
				float dy = derivY.data[indexY];

				intensity.data[indexI] = (float)Math.sqrt(dx*dx + dy*dy);
			}
		}
	}

	static public void intensityAbs( ImageFloat32 derivX , ImageFloat32 derivY , ImageFloat32 intensity )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexI = intensity.startIndex + y*intensity.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexI++ ) {

				intensity.data[indexI] = Math.abs(derivX.data[indexX]) +  Math.abs(derivY.data[indexY]);
			}
		}
	}

	static public void direction( ImageFloat32 derivX , ImageFloat32 derivY , ImageFloat32 angle )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexA = angle.startIndex + y*angle.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexA++ ) {
				float dx = derivX.data[indexX];
				float dy = derivY.data[indexY];

				// compute the angle while avoiding divided by zero errors
				angle.data[indexA] = Math.abs(dx) < 1e-10f ? (float)(Math.PI/2.0) : (float)Math.atan(dy/dx);
			}
		}
	}

	static public void intensityE( ImageSInt16 derivX , ImageSInt16 derivY , ImageFloat32 intensity )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexI = intensity.startIndex + y*intensity.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexI++ ) {
				int dx = derivX.data[indexX];
				int dy = derivY.data[indexY];

				intensity.data[indexI] = (float)Math.sqrt(dx*dx + dy*dy);
			}
		}
	}

	static public void intensityAbs( ImageSInt16 derivX , ImageSInt16 derivY , ImageFloat32 intensity )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexI = intensity.startIndex + y*intensity.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexI++ ) {

				intensity.data[indexI] = Math.abs(derivX.data[indexX]) +  Math.abs(derivY.data[indexY]);
			}
		}
	}

	static public void direction( ImageSInt16 derivX , ImageSInt16 derivY , ImageFloat32 angle )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexA = angle.startIndex + y*angle.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexA++ ) {
				int dx = derivX.data[indexX];
				int dy = derivY.data[indexY];

				// compute the angle while avoiding divided by zero errors
				angle.data[indexA] = dx == 0 ? (float)(Math.PI/2.0) : (float)Math.atan((double)dy/(double)dx);
			}
		}
	}

	static public void intensityE( ImageSInt32 derivX , ImageSInt32 derivY , ImageFloat32 intensity )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexI = intensity.startIndex + y*intensity.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexI++ ) {
				int dx = derivX.data[indexX];
				int dy = derivY.data[indexY];

				intensity.data[indexI] = (float)Math.sqrt(dx*dx + dy*dy);
			}
		}
	}

	static public void intensityAbs( ImageSInt32 derivX , ImageSInt32 derivY , ImageFloat32 intensity )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexI = intensity.startIndex + y*intensity.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexI++ ) {

				intensity.data[indexI] = Math.abs(derivX.data[indexX]) +  Math.abs(derivY.data[indexY]);
			}
		}
	}

	static public void direction( ImageSInt32 derivX , ImageSInt32 derivY , ImageFloat32 angle )
	{
		final int w = derivX.width;
		final int h = derivY.height;

		for( int y = 0; y < h; y++ ) {
			int indexX = derivX.startIndex + y*derivX.stride;
			int indexY = derivY.startIndex + y*derivY.stride;
			int indexA = angle.startIndex + y*angle.stride;

			int end = indexX + w;
			for( ; indexX < end; indexX++ , indexY++ , indexA++ ) {
				int dx = derivX.data[indexX];
				int dy = derivY.data[indexY];

				// compute the angle while avoiding divided by zero errors
				angle.data[indexA] = dx == 0 ? (float)(Math.PI/2.0) : (float)Math.atan((double)dy/(double)dx);
			}
		}
	}


}
