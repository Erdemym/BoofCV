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

package boofcv.alg.distort.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;
import boofcv.misc.CodeGeneratorUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author Peter Abeles
 */
public class GeneratorImplImageDistort extends CodeGeneratorBase {
	String className;

	PrintStream out;
	AutoTypeImage image;

	@Override
	public void generate() throws FileNotFoundException {
		createType(AutoTypeImage.F32);
		createType(AutoTypeImage.I8);
		createType(AutoTypeImage.I16);
		createType(AutoTypeImage.S32);
	}

	private void createType( AutoTypeImage type ) throws FileNotFoundException {
		className = "ImplImageDistort_"+type.name();
		image = type;

		createFile();
	}

	private void createFile() throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(className + ".java"));
		printPreamble();
		printInterpolation(false);
		printInterpolation(true);
		out.println("}");
	}

	private void printPreamble() {

		String imageName = image.getImageName();

		out.print(CodeGeneratorUtil.copyright);
		out.print("package gecv.alg.distort.impl;\n" +
				"\n" +
				"import gecv.alg.InputSanityCheck;\n" +
				"import gecv.alg.interpolate.InterpolatePixel;\n" +
				"import gecv.alg.distort.ImageDistort;\n" +
				"import gecv.struct.image."+imageName+";\n" +
				"import gecv.struct.distort.PixelTransform;\n" +
				"\n" +
				"\n" +
				"/**\n" +
				" * <p>Implementation of {@link gecv.alg.distort.ImageDistort}.</p>\n" +
				" *\n" +
				" * <p>\n" +
				" * DO NOT MODIFY: Generated by {@link gecv.alg.distort.impl.GeneratorImplImageDistort}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"@SuppressWarnings({\"UnnecessaryLocalVariable\"})\n");
		if( image.isInteger() ) {
			out.print("public class "+className+"<T extends "+imageName+"> implements ImageDistort<T> {\n");
			imageName = "T";
		} else {
			out.print("public class "+className+" implements ImageDistort<"+imageName+"> {\n");
		}

		out.print("\n" +
				"\t// transform from dst to src image\n" +
				"\tprivate PixelTransform dstToSrc;\n" +
				"\t// sub pixel interpolation\n" +
				"\tprivate InterpolatePixel<"+imageName+"> interp;\n" +
				"\n" +
				"\tpublic "+className+"(PixelTransform dstToSrc, InterpolatePixel<"+imageName+"> interp) {\n" +
				"\t\tthis.dstToSrc = dstToSrc;\n" +
				"\t\tthis.interp = interp;\n" +
				"\t}\n"+
				"\n" +
				"\t@Override\n" +
				"\tpublic void setModel(PixelTransform dstToSrc) {\n" +
				"\t\tthis.dstToSrc = dstToSrc;\n" +
				"\t}\n\n");
	}

	private void printInterpolation( boolean defaultValue )
	{
		String imageName = image.getImageName();
		String typeCast = image.isInteger() ? "("+image.getDataType()+")" : "";

		if( image.isInteger() ) {
			imageName = "T";
		}

		out.print("\t@Override\n");
		if( defaultValue ) {
			out.print("\tpublic void apply( "+imageName+" srcImg , "+imageName+" dstImg , Number value ) {\n");
		} else {
			out.print("\tpublic void apply( "+imageName+" srcImg , "+imageName+" dstImg ) {\n");
		}
		out.print("\t\tinterp.setImage(srcImg);\n" +
				"\n");
		if( defaultValue ) {
			if( image.isInteger() )
				out.print("\t\tint valueF = value.intValue();\n\n");
			else
				out.print("\t\tfloat valueF = value.floatValue();\n\n");
		}
		out.print("\t\tfinal float widthF = srcImg.getWidth();\n" +
				"\t\tfinal float heightF = srcImg.getHeight();\n" +
				"\n" +
				"\t\tfor( int y = 0; y < dstImg.height; y++ ) {\n" +
				"\t\t\tint indexDst = dstImg.startIndex + dstImg.stride*y;\n" +
				"\t\t\tfor( int x = 0; x < dstImg.width; x++ , indexDst++ ) {\n" +
				"\t\t\t\tdstToSrc.compute(x,y);\n" +
				"\n" +
				"\t\t\t\tfinal float sx = dstToSrc.distX;\n" +
				"\t\t\t\tfinal float sy = dstToSrc.distY;\n" +
				"\n" +
				"\t\t\t\tif( sx < 0f || sx >= widthF || sy < 0f || sy >= heightF ) {\n");
		if( defaultValue ) {
			out.print("\t\t\t\t\tdstImg.data[indexDst] = "+typeCast+"valueF;\n");
		}

		out.print("\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tdstImg.data[indexDst] = "+typeCast+"interp.get(sx,sy);\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GeneratorImplImageDistort gen = new GeneratorImplImageDistort();
		gen.generate();
	}
}
