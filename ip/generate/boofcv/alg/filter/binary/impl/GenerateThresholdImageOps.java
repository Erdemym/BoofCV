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

package boofcv.alg.filter.binary.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;
import boofcv.misc.CodeGeneratorUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author Peter Abeles
 */
public class GenerateThresholdImageOps extends CodeGeneratorBase {
	String className = "ThresholdImageOps";

	PrintStream out;

	public GenerateThresholdImageOps() throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(className + ".java"));
	}

	@Override
	public void generate() throws FileNotFoundException {
		printPreamble();

		printAll(AutoTypeImage.F32);
		printAll(AutoTypeImage.F64);
		printAll(AutoTypeImage.U8);
		printAll(AutoTypeImage.S16);
		printAll(AutoTypeImage.U16);
		printAll(AutoTypeImage.S32);

		out.print("\n" +
				"}\n");
	}

	private void printPreamble() {
		out.print(CodeGeneratorUtil.copyright);
		out.print("package gecv.alg.filter.binary;\n" +
				"\n" +
				"import gecv.alg.InputSanityCheck;\n" +
				"import gecv.struct.image.*;\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Operations for thresholding images and converting them into a binary image.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * WARNING: Do not modify.  Automatically generated by {@link gecv.alg.filter.binary.impl.GenerateThresholdImageOps}.\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"public class "+className+" {\n\n");

	}

	public void printAll( AutoTypeImage imageIn ) {
		printThreshold(imageIn);
		printThresholdBlobs(imageIn);
	}

	public void printThreshold( AutoTypeImage imageIn ) {
		out.print("\t/**\n" +
				"\t * Applies a global threshold across the whole image.  Pixels which are\n" +
				"\t * considered in the set defined by the threshold are set to 1, all others\n" +
				"\t * are set to zero.  If the down flag is set to true then the inlier set\n" +
				"\t * is defined as <= to the the threshold and >= if true.\n" +
				"\t *\n" +
				"\t * @param input Input image. Not modified.\n" +
				"\t * @param output Binary output image. If null a new image will be declared. Modified.\n" +
				"\t * @param threshold threshold value.\n" +
				"\t * @param down If true inliers are below the threshold and false they are above the threshold.\n" +
				"\t * @return Output image.\n" +
				"\t */\n" +
				"\tpublic static ImageUInt8 threshold( "+imageIn.getImageName()+" input , ImageUInt8 output ,\n" +
				"\t\t\t\t\t\t\t\t\t\t"+imageIn.getSumType()+" threshold , boolean down )\n" +
				"\t{\n" +
				"\t\toutput = InputSanityCheck.checkDeclare(input,output,ImageUInt8.class);\n" +
				"\n" +
				"\t\tif( down ) {\n" +
				"\t\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\t\tint indexIn = input.startIndex + y*input.stride;\n" +
				"\t\t\t\tint indexOut = output.startIndex + y*output.stride;\n" +
				"\n" +
				"\t\t\t\tint end = indexIn + input.width;\n" +
				"\n" +
				"\t\t\t\tfor( ; indexIn < end; indexIn++ , indexOut++ ) {\n" +
				"\t\t\t\t\tif( (input.data[indexIn]"+imageIn.getBitWise()+") <= threshold )\n" +
				"\t\t\t\t\t\toutput.data[indexOut] = 1;\n" +
				"\t\t\t\t\telse\n" +
				"\t\t\t\t\t\toutput.data[indexOut] = 0;\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\t\t} else {\n" +
				"\t\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\t\tint indexIn = input.startIndex + y*input.stride;\n" +
				"\t\t\t\tint indexOut = output.startIndex + y*output.stride;\n" +
				"\n" +
				"\t\t\t\tint end = indexIn + input.width;\n" +
				"\n" +
				"\t\t\t\tfor( ; indexIn < end; indexIn++ , indexOut++ ) {\n" +
				"\t\t\t\t\tif( (input.data[indexIn]"+imageIn.getBitWise()+") >= threshold )\n" +
				"\t\t\t\t\t\toutput.data[indexOut] = 1;\n" +
				"\t\t\t\t\telse\n" +
				"\t\t\t\t\t\toutput.data[indexOut] = 0;\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\n" +
				"\t\treturn output;\n" +
				"\t}\n\n");
	}

	public void printThresholdBlobs( AutoTypeImage imageIn ) {
		out.print("\t/**\n" +
				"\t * <p>\n" +
				"\t * Marks which labeled blobs are contained within the inlier set.  If a blob\n" +
				"\t * is inside the inlier set then its element in the 'results' array is set to its own ID number.  Otherwise\n" +
				"\t * that element is set to zero.\n" +
				"\t * </p>\n" +
				"\t * \n" +
				"\t * @param input Original input image with intensity values.  Not modified.\n" +
				"\t * @param labeled Labeled binary image.\n" +
				"\t * @param results Where the inlier blobs are indicated.\n" +
				"\t * @param numBlobs The number of blobs.\n" +
				"\t * @param threshold Threshold used to define inlier set.\n" +
				"\t * @param down If the threshold is up or down.\n" +
				"\t */\n" +
				"\tpublic static void thresholdBlobs( "+imageIn.getImageName()+" input , ImageSInt32 labeled ,\n" +
				"\t\t\t\t\t\t\t\t\t   int results[] , int numBlobs , \n" +
				"\t\t\t\t\t\t\t\t\t   "+imageIn.getSumType()+" threshold , boolean down ) {\n" +
				"\t\tfor( int i = 0; i < numBlobs; i++ ) {\n" +
				"\t\t\tresults[i] = 0;\n" +
				"\t\t}\n" +
				"\t\t\n" +
				"\t\tif( down ) {\n" +
				"\t\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\t\tint indexIn = input.startIndex + y*input.stride;\n" +
				"\t\t\t\tint indexWork = labeled.startIndex + y*labeled.stride;\n" +
				"\n" +
				"\t\t\t\tint end = indexIn + input.width;\n" +
				"\n" +
				"\t\t\t\tfor( ; indexIn < end; indexIn++ , indexWork++ ) {\n" +
				"\t\t\t\t\tif( (input.data[indexIn]"+imageIn.getBitWise()+") <= threshold ) {\n" +
				"\t\t\t\t\t\tint val = labeled.data[indexWork];\n" +
				"\t\t\t\t\t\tresults[val] = val;\n" +
				"\t\t\t\t\t}\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\t\t} else {\n" +
				"\t\t\tfor( int y = 0; y < input.height; y++ ) {\n" +
				"\t\t\t\tint indexIn = input.startIndex + y*input.stride;\n" +
				"\t\t\t\tint indexWork = labeled.startIndex + y*labeled.stride;\n" +
				"\n" +
				"\t\t\t\tint end = indexIn + input.width;\n" +
				"\n" +
				"\t\t\t\tfor( ; indexIn < end; indexIn++ , indexWork++ ) {\n" +
				"\t\t\t\t\tif( (input.data[indexIn]"+imageIn.getBitWise()+") >= threshold ) {\n" +
				"\t\t\t\t\t\tint val = labeled.data[indexWork];\n" +
				"\t\t\t\t\t\tresults[val] = val;\n" +
				"\t\t\t\t\t}\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public void printHysteresisLabel( AutoTypeImage imageIn , int rule ) {
		out.print("\t/**\n" +
				"\t * <p>\n" +
				"\t * Hysteresis thresholding and blob labeling with a connect-"+rule+" rule. The input image is thresholded and the resulting\n" +
				"\t * blobs are labeled.\n" +
				"\t * </p>\n" +
				"\t *\n" +
				"\t * <p>\n" +
				"\t * Hysteresis thresholding works by first detecting if a pixel is within a more stringent threshold.  If it is\n" +
				"\t * then a less stringent threshold is used for all the connected pixels. The threshold direction determines\n" +
				"\t * if the lower or upper threshold is more or less stringent.  When thresholding down the the lower threshold\n" +
				"\t * is more stringent and the upper less. The opposite is true for when being thresholded up.\n" +
				"\t * </p>\n" +
				"\t *\n" +
				"\t * @param input Input intensity image. Not modified.\n" +
				"\t * @param output Output labeled binary image. If null a new instance will be declared. Modified.\n" +
				"\t * @param lowerThreshold Lower threshold.\n" +
				"\t * @param upperThreshold Upper threshold.\n" +
				"\t * @param down If it is being thresholded down or up.\n" +
				"\t * @param work Work image which stores intermediate results and is the same size as the input image.  If null one will be declared internally.\n" +
				"\t * @return Labeled binary image.\n" +
				"\t */\n" +
				"\tpublic static ImageSInt32 hysteresisLabel"+rule+"( "+imageIn.getImageName()+" input , ImageSInt32 output ,\n" +
				"\t\t\t\t\t\t\t\t\t\t\t\tfloat lowerThreshold , float upperThreshold , boolean down ,\n" +
				"\t\t\t\t\t\t\t\t\t\t\t\tImageUInt8 work )\n" +
				"\t{\n" +
				"\t\toutput = InputSanityCheck.checkDeclare(input,output,ImageSInt32.class);\n" +
				"\t\twork = InputSanityCheck.checkDeclare(input,work,ImageUInt8.class);\n" +
				"\n" +
				"\t\tif( down ) {\n" +
				"\t\t\tthreshold(input,work,upperThreshold,true);\n" +
				"\t\t\tint numBlobs = BinaryImageOps.labelBlobs"+rule+"(work,output,null);\n" +
				"\n" +
				"\t\t\tint relabel[] = new int[numBlobs];\n" +
				"\t\t\tthresholdBlobs(input,output,relabel,numBlobs,lowerThreshold,true);\n" +
				"\t\t\tImplBinaryBlobLabeling.minimizeBlobID(relabel,numBlobs);\n" +
				"\t\t\tBinaryImageOps.relabel(output,relabel);\n" +
				"\t\t} else {\n" +
				"\t\t\tthreshold(input,work,lowerThreshold,false);\n" +
				"\t\t\tint numBlobs = BinaryImageOps.labelBlobs"+rule+"(work,output,null);\n" +
				"\n" +
				"\t\t\tint relabel[] = new int[numBlobs];\n" +
				"\t\t\tthresholdBlobs(input,output,relabel,numBlobs,upperThreshold,false);\n" +
				"\t\t\tBinaryImageOps.relabel(output,relabel);\n" +
				"\t\t}\n" +
				"\n" +
				"\t\treturn output;\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateThresholdImageOps app = new GenerateThresholdImageOps();
		app.generate();
	}
}
