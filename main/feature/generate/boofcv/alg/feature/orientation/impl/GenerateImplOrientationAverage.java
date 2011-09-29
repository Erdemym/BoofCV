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

package boofcv.alg.feature.orientation.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author Peter Abeles
 */
public class GenerateImplOrientationAverage extends CodeGeneratorBase {
	String className;

	AutoTypeImage imageType;

	@Override
	public void generate() throws FileNotFoundException {
		printClass(AutoTypeImage.F32);
		printClass(AutoTypeImage.S16);
		printClass(AutoTypeImage.S32);
	}

	private void printClass( AutoTypeImage imageType ) throws FileNotFoundException {
		this.imageType = imageType;
		className = "ImplOrientationAverage_"+imageType.getAbbreviatedType();
		out = new PrintStream(new FileOutputStream(className + ".java"));
		printPreamble();
		printFunctions();

		out.print("}\n");
	}

	private void printPreamble() throws FileNotFoundException {
		setOutputFile(className);
		out.print("import boofcv.alg.feature.describe.OrientationAverage;\n" +
				"import boofcv.struct.image."+imageType.getImageName()+";\n" +
				"\n" +
				"\n" +
				"/** \n" +
				" * <p>\n" +
				" * Implementation of {@link OrientationAverage} for a specific image type.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * WARNING: Do not modify.  Automatically generated by {@link GenerateImplOrientationAverage}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+" extends OrientationAverage<"+imageType.getImageName()+"> {\n" +
				"\t\n" +
				"\tpublic "+className+"(boolean weighted) {\n" +
				"\t\tsuper(weighted);\n" +
				"\t}\n\n");
	}

	private void printFunctions() {
		printVarious();
		printUnweighted();
		printWeighted();
	}

	private void printVarious() {
		out.print("\t@Override\n" +
				"\tpublic Class<"+imageType.getImageName()+"> getImageType() {\n" +
				"\t\treturn "+imageType.getImageName()+".class;\n" +
				"\t}\n\n");
	}

	private void printUnweighted() {
		String bitWise = imageType.getBitWise();

		out.print("\t@Override\n" +
				"\tprotected double computeUnweightedScore()\n" +
				"\t{\n" +
				"\t\tfloat sumX=0,sumY=0;\n" +
				"\n" +
				"\t\tfor( int y = rect.y0; y < rect.y1; y++ ) {\n" +
				"\t\t\tint indexX = derivX.startIndex + derivX.stride*y + rect.x0;\n" +
				"\t\t\tint indexY = derivY.startIndex + derivY.stride*y + rect.x0;\n" +
				"\n" +
				"\t\t\tfor( int x = rect.x0; x < rect.x1; x++ , indexX++ , indexY++ ) {\n" +
				"\t\t\t\tsumX += derivX.data[indexX]"+bitWise+";\n" +
				"\t\t\t\tsumY += derivY.data[indexY]"+bitWise+";\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\treturn Math.atan2(sumY,sumX);\n" +
				"\t}\n\n");
	}
	private void printWeighted() {
		String bitWise = imageType.getBitWise();
		out.print("\t@Override\n" +
				"\tprotected double computeWeightedScore(int c_x, int c_y)\n" +
				"\t{\n" +
				"\t\tfloat sumX=0,sumY=0;\n" +
				"\t\t\n" +
				"\t\tfor( int y = rect.y0; y < rect.y1; y++ ) {\n" +
				"\t\t\tint indexX = derivX.startIndex + derivX.stride*y + rect.x0;\n" +
				"\t\t\tint indexY = derivY.startIndex + derivY.stride*y + rect.x0;\n" +
				"\t\t\tint indexW = (y-c_y+radiusScale)*weights.width + rect.x0-c_x+radiusScale;\n" +
				"\n" +
				"\t\t\tfor( int x = rect.x0; x < rect.x1; x++ , indexX++ , indexY++ , indexW++ ) {\n" +
				"\t\t\t\tfloat w = weights.data[indexW];\n" +
				"\n" +
				"\t\t\t\tsumX += w * derivX.data[indexX]"+bitWise+";\n" +
				"\t\t\t\tsumY += w * derivY.data[indexY]"+bitWise+";\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t\treturn Math.atan2(sumY,sumX);\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplOrientationAverage app = new GenerateImplOrientationAverage();
		app.generate();
	}
}
