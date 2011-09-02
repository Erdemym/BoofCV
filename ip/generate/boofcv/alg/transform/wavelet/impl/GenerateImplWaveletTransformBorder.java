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

package boofcv.alg.transform.wavelet.impl;

import boofcv.misc.AutoTypeImage;
import boofcv.misc.CodeGeneratorBase;
import boofcv.misc.CodeGeneratorUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * @author Peter Abeles
 */
public class GenerateImplWaveletTransformBorder extends CodeGeneratorBase {
	String className = "ImplWaveletTransformBorder";

	PrintStream out;

	AutoTypeImage imageIn;
	AutoTypeImage imageOut;
	String genName;
	String sumType;
	String bitWise;
	String outputCast;

	public GenerateImplWaveletTransformBorder() throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(className + ".java"));
	}

	@Override
	public void generate() throws FileNotFoundException {
		printPreamble();

		printFuncs(AutoTypeImage.F32, AutoTypeImage.F32);
		printFuncs(AutoTypeImage.S32, AutoTypeImage.S32);

		out.print("\n" +
				"}\n");
	}

	private void printPreamble() {
		out.print(CodeGeneratorUtil.copyright);
		out.print("package gecv.alg.wavelet.impl;\n" +
				"\n" +
				"import gecv.alg.wavelet.UtilWavelet;\n" +
				"import gecv.core.image.border.BorderIndex1D;\n" +
				"import gecv.struct.image.*;\n" +
				"import gecv.struct.wavelet.WlCoef;\n" +
				"import gecv.struct.wavelet.WlCoef_F32;\n" +
				"import gecv.struct.wavelet.WlCoef_I32;\n" +
				"import gecv.struct.wavelet.WlBorderCoef;\n" +
				"\n" +
				"\n" +
				"/**\n" +
				" * <p>\n" +
				" * Performs the wavelet transform just around the image border.  Should be called in conjunction\n" +
				" * with {@link ImplWaveletTransformInner} or similar functions.  Must be called after the inner\n" +
				" * portion has been computed because the \"inner\" functions modify the border during the inverse\n" +
				" * transform.\n" +
				" * </p>\n" +
				" *\n" +
				" * <p>\n" +
				" * For the inverse transform the inner transform must be called before the border is computed.\n" +
				" * Due to how the inverse is computed some of the output values will be added to border.  The values\n" +
				" * computed in these inverse functions add to that.\n" +
				" * </p>\n" +
				" * \n" +
				" * <p>\n" +
				" * DO NOT MODIFY: This class was automatically generated by {@link gecv.alg.wavelet.impl.GenerateImplWaveletTransformBorder}\n" +
				" * </p>\n" +
				" *\n" +
				" * @author Peter Abeles\n" +
				" */\n" +
				"@SuppressWarnings({\"ForLoopReplaceableByForEach\"})\n" +
				"public class ImplWaveletTransformBorder {\n\n");
	}

	private void printFuncs( AutoTypeImage imageIn , AutoTypeImage imageOut ) {
		this.imageIn = imageIn;
		this.imageOut = imageOut;

		if( imageIn.isInteger() )
			genName = "I32";
		else
			genName = "F"+imageIn.getNumBits();

		sumType = imageIn.getSumType();
		bitWise = imageIn.getBitWise();

		if( sumType.compareTo(imageOut.getDataType()) == 0 ) {
			outputCast = "";
		} else {
			outputCast = "("+imageOut.getDataType()+")";
		}

		printHorizontal();
		printVertical();
		printHorizontalInverse();
		printVerticalInverse();
	}

	private void printHorizontal() {
		out.print("\tpublic static void horizontal( BorderIndex1D border , WlCoef_"+genName+" coefficients , "+imageIn.getImageName()+" input , "+imageOut.getImageName()+" output )\n" +
				"\t{\n" +
				"\t\tfinal int offsetA = coefficients.offsetScaling;\n" +
				"\t\tfinal int offsetB = coefficients.offsetWavelet;\n" +
				"\t\tfinal "+sumType+"[] alpha = coefficients.scaling;\n" +
				"\t\tfinal "+sumType+"[] beta = coefficients.wavelet;\n" +
				"\n" +
				"\t\tborder.setLength(input.width + input.width%2);\n" +
				"\n" +
				"\t\tfinal boolean isLarger = output.width > input.width;\n" +
				"\t\tfinal int width = input.width+input.width%2;\n" +
				"\t\tfinal int height = input.height;\n" +
				"\t\tfinal int lowerBorder = UtilWavelet.borderForwardLower(coefficients);\n" +
				"\t\tfinal int upperBorder = input.width - UtilWavelet.borderForwardUpper(coefficients,input.width);\n" +
				"\n" +
				"\t\tfor( int y = 0; y < height; y++ ) {\n" +
				"\t\t\tfor( int x = 0; x < lowerBorder; x += 2 ) {\n" +
				"\t\t\t\t"+sumType+" scale = 0;\n" +
				"\t\t\t\t"+sumType+" wavelet = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = 0; i < alpha.length; i++ ) {\n" +
				"\t\t\t\t\tint xx = border.getIndex(x+i+offsetA);\n" +
				"\t\t\t\t\tif( isLarger && xx >= input.width )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\tscale += input.get(xx,y)*alpha[i];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tfor( int i = 0; i < beta.length; i++ ) {\n" +
				"\t\t\t\t\tint xx = border.getIndex(x+i+offsetB);\n" +
				"\t\t\t\t\tif( isLarger && xx >= input.width )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\twavelet += input.get(xx,y)*beta[i];\n" +
				"\t\t\t\t}\n" +
				"\n");

		if( imageIn.isInteger() ) {
			out.print("\t\t\t\tscale = 2*scale/coefficients.denominatorScaling;\n" +
					"\t\t\t\twavelet = 2*wavelet/coefficients.denominatorWavelet;\n\n");
		}

		out.print("\t\t\t\tint outX = x/2;\n" +
				"\n" +
				"\t\t\t\toutput.set(outX,y,scale);\n" +
				"\t\t\t\toutput.set(output.width/2 + outX , y , wavelet );\n" +
				"\t\t\t}\n" +
				"\t\t\tfor( int x = upperBorder; x < width; x += 2 ) {\n" +
				"\t\t\t\t"+sumType+" scale = 0;\n" +
				"\t\t\t\t"+sumType+" wavelet = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = 0; i < alpha.length; i++ ) {\n" +
				"\t\t\t\t\tint xx = border.getIndex(x+i+offsetA);\n" +
				"\t\t\t\t\tif( isLarger && xx >= input.width )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\tscale += input.get(xx,y)*alpha[i];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tfor( int i = 0; i < beta.length; i++ ) {\n" +
				"\t\t\t\t\tint xx = border.getIndex(x+i+offsetB);\n" +
				"\t\t\t\t\tif( isLarger && xx >= input.width )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\twavelet += input.get(xx,y)*beta[i];\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tint outX = x/2;\n" +
				"\n");

		if( imageIn.isInteger() ) {
			out.print("\t\t\t\tscale = 2*scale/coefficients.denominatorScaling;\n" +
					"\t\t\t\twavelet = 2*wavelet/coefficients.denominatorWavelet;\n\n");
		}

		out.print("\t\t\t\toutput.set(outX,y,scale);\n" +
				"\t\t\t\toutput.set(output.width/2 + outX , y , wavelet );\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printVertical() {
		out.print("\tpublic static void vertical( BorderIndex1D border , WlCoef_"+genName+" coefficients , "+imageIn.getImageName()+" input , "+imageOut.getImageName()+" output )\n" +
				"\t{\n" +
				"\t\tfinal int offsetA = coefficients.offsetScaling;\n" +
				"\t\tfinal int offsetB = coefficients.offsetWavelet;\n" +
				"\t\tfinal "+sumType+"[] alpha = coefficients.scaling;\n" +
				"\t\tfinal "+sumType+"[] beta = coefficients.wavelet;\n" +
				"\n" +
				"\t\tborder.setLength(input.height + input.height%2);\n" +
				"\n" +
				"\t\tfinal boolean isLarger = output.height > input.height;\n" +
				"\t\tfinal int width = input.width;\n" +
				"\t\tfinal int height = input.height+input.height%2;\n" +
				"\t\tfinal int lowerBorder = UtilWavelet.borderForwardLower(coefficients);\n" +
				"\t\tfinal int upperBorder = input.height - UtilWavelet.borderForwardUpper(coefficients,input.height);\n" +
				"\n" +
				"\t\tfor( int x = 0; x < width; x++) {\n" +
				"\t\t\tfor( int y = 0; y < lowerBorder; y += 2 ) {\n" +
				"\t\t\t\t"+sumType+" scale = 0;\n" +
				"\t\t\t\t"+sumType+" wavelet = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = 0; i < alpha.length; i++ ) {\n" +
				"\t\t\t\t\tint yy = border.getIndex(y+i+offsetA);\n" +
				"\t\t\t\t\tif( isLarger && yy >= input.height )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\tscale += input.get(x,yy)*alpha[i];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tfor( int i = 0; i < beta.length; i++ ) {\n" +
				"\t\t\t\t\tint yy = border.getIndex(y+i+offsetB);\n" +
				"\t\t\t\t\tif( isLarger && yy >= input.height )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\twavelet += input.get(x,yy)*beta[i];\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tint outY = y/2;\n" +
				"\n");

		if( imageIn.isInteger() ) {
			out.print("\t\t\t\tscale = 2*scale/coefficients.denominatorScaling;\n" +
					"\t\t\t\twavelet = 2*wavelet/coefficients.denominatorWavelet;\n\n");
		}

		out.print("\t\t\t\toutput.set(x , outY,scale);\n" +
				"\t\t\t\toutput.set(x , output.height/2 + outY , wavelet );\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tfor( int y = upperBorder; y < height; y += 2 ) {\n" +
				"\t\t\t\t"+sumType+" scale = 0;\n" +
				"\t\t\t\t"+sumType+" wavelet = 0;\n" +
				"\n" +
				"\t\t\t\tfor( int i = 0; i < alpha.length; i++ ) {\n" +
				"\t\t\t\t\tint yy = border.getIndex(y+i+offsetA);\n" +
				"\t\t\t\t\tif( isLarger && yy >= input.height )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\tscale += input.get(x,yy)*alpha[i];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t\tfor( int i = 0; i < beta.length; i++ ) {\n" +
				"\t\t\t\t\tint yy = border.getIndex(y+i+offsetB);\n" +
				"\t\t\t\t\tif( isLarger && yy >= input.height )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\twavelet += input.get(x,yy)*beta[i];\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tint outY = y/2;\n" +
				"\n");

		if( imageIn.isInteger() ) {
			out.print("\t\t\t\tscale = 2*scale/coefficients.denominatorScaling;\n" +
					"\t\t\t\twavelet = 2*wavelet/coefficients.denominatorWavelet;\n\n");
		}

		out.print("\t\t\t\toutput.set(x , outY,scale);\n" +
				"\t\t\t\toutput.set(x , output.height/2 + outY , wavelet );\n" +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printHorizontalInverse() {

		String outputSum;

		if( imageIn.isInteger() ) {
			outputSum = "\t\t\t\toutput.data[ indexDst + x ] = "+outputCast+"UtilWavelet.round(trends[x]*f + details[x]*e , ef2 , ef);\n";
		} else {
			outputSum = "\t\t\t\toutput.data[ indexDst + x ] = "+outputCast+"(trends[x] + details[x]);\n";
		}

		out.print("\tpublic static void horizontalInverse( BorderIndex1D border , WlBorderCoef<WlCoef_"+genName+"> desc , "+imageIn.getImageName()+" input , "+imageOut.getImageName()+" output )\n" +
				"\t{\n" +
				"\t\t"+sumType+" []trends = new "+sumType+"[ input.width ];\n" +
				"\t\t"+sumType+" []details = new "+sumType+"[ input.width ];\n" +
				"\n" +
				"\t\tfinal int height = output.height;\n" +
				"\t\tfinal int paddedWidth = output.width + output.width%2;\n" +
				"\n" +
				"\t\tWlCoef inner = desc.getInnerCoefficients();\n" +
				"\t\t// need to convolve coefficients that influence the ones being updated\n" +
				"\t\tint lowerExtra = -Math.min(inner.offsetScaling,inner.offsetWavelet);\n" +
				"\t\tint upperExtra = Math.max(inner.getScalingLength()+inner.offsetScaling,inner.getWaveletLength()+inner.offsetWavelet);\n" +
				"\t\tlowerExtra += lowerExtra%2;\n" +
				"\t\tupperExtra += upperExtra%2;\n" +
				"\n" +
				"\t\tint lowerBorder = (UtilWavelet.borderInverseLower(desc,border)+lowerExtra)/2;\n" +
				"\t\tint upperBorder = (UtilWavelet.borderInverseUpper(desc,border,output.width)+upperExtra)/2;\n" +
				"\n" +
				"\t\tboolean isLarger = input.width >= output.width;\n" +
				"\t\t\n" +
				"\t\t// where updated wavelet values are stored\n" +
				"\t\tint lowerCompute = lowerBorder*2-lowerExtra;\n" +
				"\t\tint upperCompute = upperBorder*2-upperExtra;\n" +
				"\n" +
				"\t\tint indexes[] = new int[lowerBorder+upperBorder];\n" +
				"\t\tfor( int i = 0; i < lowerBorder; i++ )\n" +
				"\t\t\tindexes[i] = i*2;\n" +
				"\t\tfor( int i = lowerBorder; i < indexes.length; i++ )\n" +
				"\t\t\tindexes[i] = paddedWidth-(indexes.length-i)*2;\n" +
				"\n" +
				"\t\tborder.setLength(output.width+output.width%2);\n" +
				"\n");

		if( imageIn.isInteger() ) {
			out.print("\t\tWlCoef_"+genName+" coefficients = desc.getInnerCoefficients();\n");
			out.print("\t\tfinal int e = coefficients.denominatorScaling*2;\n" +
					"\t\tfinal int f = coefficients.denominatorWavelet*2;\n" +
					"\t\tfinal int ef = e*f;\n" +
					"\t\tfinal int ef2 = ef/2;\n");
		} else {
			out.print("\t\tWlCoef_"+genName+" coefficients;\n");
		}

		out.print("\n" +
				"\t\tfor( int y = 0; y < height; y++ ) {\n" +
				"\n" +
				"\t\t\t// initialize details and trends arrays\n" +
				"\t\t\tfor( int i = 0; i < indexes.length; i++ ) {\n" +
				"\t\t\t\tint x = indexes[i];\n" +
				"\t\t\t\tdetails[x] = 0; trends[x] = 0;\n" +
				"\t\t\t\tx++;\n" +
				"\t\t\t\tdetails[x] = 0; trends[x] = 0;\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tfor( int i = 0; i < indexes.length; i++ ) {\n" +
				"\t\t\t\tint x = indexes[i];\n" +
				"\t\t\t\tfloat a = input.get(x/2,y);\n" +
				"\t\t\t\tfloat d = input.get(input.width/2+x/2,y);\n" +
				"\n" +
				"\t\t\t\tif( x < lowerBorder ) {\n" +
				"\t\t\t\t\tcoefficients = desc.getBorderCoefficients(x);\n" +
				"\t\t\t\t} else if( x >= upperBorder ) {\n" +
				"\t\t\t\t\tcoefficients = desc.getBorderCoefficients(x-paddedWidth);\n" +
				"\t\t\t\t} else {\n" +
				"\t\t\t\t\tcoefficients = desc.getInnerCoefficients();\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tfinal int offsetA = coefficients.offsetScaling;\n" +
				"\t\t\t\tfinal int offsetB = coefficients.offsetWavelet;\n" +
				"\t\t\t\tfinal "+sumType+"[] alpha = coefficients.scaling;\n" +
				"\t\t\t\tfinal "+sumType+"[] beta = coefficients.wavelet;\n" +
				"\n" +
				"\t\t\t\t// add the trend\n" +
				"\t\t\t\tfor( int j = 0; j < alpha.length; j++ ) {\n" +
				"\t\t\t\t\t// if an odd image don't update the outer edge\n" +
				"\t\t\t\t\tint xx = border.getIndex(x+offsetA+j);\n" +
				"\t\t\t\t\tif( isLarger && xx >= output.width )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\ttrends[xx] += a*alpha[j];\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\t// add the detail signal\n" +
				"\t\t\t\tfor( int j = 0; j < beta.length; j++ ) {\n" +
				"\t\t\t\t\tint xx = border.getIndex(x+offsetB+j);\n" +
				"\t\t\t\t\tif( isLarger && xx >= output.width )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\tdetails[xx] += d*beta[j];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tint indexDst = output.startIndex + y*output.stride;\n" +
				"\t\t\tfor( int x = 0; x < lowerCompute; x++ ) {\n" +
				outputSum +
				"\t\t\t}\n" +
				"\t\t\tfor( int x = paddedWidth-upperCompute; x < output.width; x++) {\n" +
				outputSum +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	private void printVerticalInverse() {

		String outputSum;

		if( imageIn.isInteger() ) {
			outputSum = "\t\t\t\toutput.data[ indexDst + y*output.stride ] = "+outputCast+"UtilWavelet.round(trends[y]*f + details[y]*e , ef2 , ef);\n";
		} else {
			outputSum = "\t\t\t\toutput.data[ indexDst + y*output.stride ] = "+outputCast+"(trends[y] + details[y]);\n";
		}

		out.print("\tpublic static void verticalInverse( BorderIndex1D border , WlBorderCoef<WlCoef_"+genName+"> desc , "+imageIn.getImageName()+" input , "+imageOut.getImageName()+" output )\n" +
				"\t{\n" +
				"\t\t"+sumType+" []trends = new "+sumType+"[ input.height ];\n" +
				"\t\t"+sumType+" []details = new "+sumType+"[ input.height ];\n" +
				"\n" +
				"\t\tfinal int width = output.width;\n" +
				"\t\tfinal int paddedHeight = output.height + output.height%2;\n" +
				"\n" +
				"\t\tWlCoef inner = desc.getInnerCoefficients();\n" +
				"\t\t// need to convolve coefficients that influence the ones being updated\n" +
				"\t\tint lowerExtra = -Math.min(inner.offsetScaling,inner.offsetWavelet);\n" +
				"\t\tint upperExtra = Math.max(inner.getScalingLength()+inner.offsetScaling,inner.getWaveletLength()+inner.offsetWavelet);\n" +
				"\t\tlowerExtra += lowerExtra%2;\n" +
				"\t\tupperExtra += upperExtra%2;\n" +
				"\n" +
				"\t\tint lowerBorder = (UtilWavelet.borderInverseLower(desc,border)+lowerExtra)/2;\n" +
				"\t\tint upperBorder = (UtilWavelet.borderInverseUpper(desc,border,output.height)+upperExtra)/2;\n" +
				"\n" +
				"\t\tboolean isLarger = input.height >= output.height;\n" +
				"\t\t\n" +
				"\t\t// where updated wavelet values are stored\n" +
				"\t\tint lowerCompute = lowerBorder*2-lowerExtra;\n" +
				"\t\tint upperCompute = upperBorder*2-upperExtra;\n" +
				"\n" +
				"\t\tint indexes[] = new int[lowerBorder+upperBorder];\n" +
				"\t\tfor( int i = 0; i < lowerBorder; i++ )\n" +
				"\t\t\tindexes[i] = i*2;\n" +
				"\t\tfor( int i = lowerBorder; i < indexes.length; i++ )\n" +
				"\t\t\tindexes[i] = paddedHeight-(indexes.length-i)*2;\n" +
				"\n" +
				"\t\tborder.setLength(output.height+output.height%2);\n" +
				"\n");

		if( imageIn.isInteger() ) {
			out.print("\t\tWlCoef_"+genName+" coefficients = desc.getInnerCoefficients();\n");
			out.print("\t\tfinal int e = coefficients.denominatorScaling*2;\n" +
					"\t\tfinal int f = coefficients.denominatorWavelet*2;\n" +
					"\t\tfinal int ef = e*f;\n" +
					"\t\tfinal int ef2 = ef/2;\n");
		} else {
			out.print("\t\tWlCoef_"+genName+" coefficients;\n");
		}

		out.print("\n" +
				"\t\tfor( int x = 0; x < width; x++ ) {\n" +
				"\n" +
				"\t\t\t// initialize details and trends arrays\n" +
				"\t\t\tfor( int i = 0; i < indexes.length; i++ ) {\n" +
				"\t\t\t\tint y = indexes[i];\n" +
				"\t\t\t\tdetails[y] = 0; trends[y] = 0;\n" +
				"\t\t\t\ty++;\n" +
				"\t\t\t\tdetails[y] = 0; trends[y] = 0;\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tfor( int i = 0; i < indexes.length; i++ ) {\n" +
				"\t\t\t\tint y = indexes[i];\n" +
				"\t\t\t\tfloat a = input.get(x,y/2);\n" +
				"\t\t\t\tfloat d = input.get(x,input.height/2+y/2);\n" +
				"\n" +
				"\t\t\t\tif( y < lowerBorder ) {\n" +
				"\t\t\t\t\tcoefficients = desc.getBorderCoefficients(y);\n" +
				"\t\t\t\t} else if( y >= upperBorder ) {\n" +
				"\t\t\t\t\tcoefficients = desc.getBorderCoefficients(y-paddedHeight);\n" +
				"\t\t\t\t} else {\n" +
				"\t\t\t\t\tcoefficients = desc.getInnerCoefficients();\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\tfinal int offsetA = coefficients.offsetScaling;\n" +
				"\t\t\t\tfinal int offsetB = coefficients.offsetWavelet;\n" +
				"\t\t\t\tfinal "+sumType+"[] alpha = coefficients.scaling;\n" +
				"\t\t\t\tfinal "+sumType+"[] beta = coefficients.wavelet;\n" +
				"\n" +
				"\t\t\t\t// add the trend\n" +
				"\t\t\t\tfor( int j = 0; j < alpha.length; j++ ) {\n" +
				"\t\t\t\t\t// if an odd image don't update the outer edge\n" +
				"\t\t\t\t\tint yy = border.getIndex(y+offsetA+j);\n" +
				"\t\t\t\t\tif( isLarger && yy >= output.height )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\ttrends[yy] += a*alpha[j];\n" +
				"\t\t\t\t}\n" +
				"\n" +
				"\t\t\t\t// add the detail signal\n" +
				"\t\t\t\tfor( int j = 0; j < beta.length; j++ ) {\n" +
				"\t\t\t\t\tint yy = border.getIndex(y+offsetB+j);\n" +
				"\t\t\t\t\tif( isLarger && yy >= output.height )\n" +
				"\t\t\t\t\t\tcontinue;\n" +
				"\t\t\t\t\tdetails[yy] += d*beta[j];\n" +
				"\t\t\t\t}\n" +
				"\t\t\t}\n" +
				"\n" +
				"\t\t\tint indexDst = output.startIndex + x;\n" +
				"\t\t\tfor( int y = 0; y < lowerCompute; y++ ) {\n" +
				outputSum +
				"\t\t\t}\n" +
				"\t\t\tfor( int y = paddedHeight-upperCompute; y < output.height; y++) {\n" +
				outputSum +
				"\t\t\t}\n" +
				"\t\t}\n" +
				"\t}\n\n");
	}

	public static void main( String args[] ) throws FileNotFoundException {
		GenerateImplWaveletTransformBorder app = new GenerateImplWaveletTransformBorder();
		app.generate();
	}
}
