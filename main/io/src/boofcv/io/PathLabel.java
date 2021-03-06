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

package boofcv.io;

/**
 * Object containing the path to a file and a label that is used to refer to the file
 * 
 * @author Peter Abeles
 */
public class PathLabel {
	public String label;
	public String []path;

	public PathLabel(String label, String path) {
		this.label = label;
		this.path = new String[]{path};
	}

	public PathLabel(String label, String ...path) {
		this.label = label;
		this.path = path;
	}

	public String getLabel() {
		return label;
	}

	public String getPath() {
		return path[0];
	}
	
	public String getPath(int index ) {
		return path[index];
	}
}
