// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query.view;

public class CodeView {
	// pathId of form org:unit:context:schema:version (a.k.a. reference)
	private final String pathId;
	private final String specification;
	private final String currentVersion;

	public static CodeView empty() {
		return new CodeView();
	}

	public static CodeView with(final String pathId) {
		return new CodeView(pathId);
	}

	public static CodeView with(final String pathId, final String specification, final String currentVersion) {
		return new CodeView(pathId, specification, currentVersion);
	}

	private CodeView() {
		this("", "", "");
	}

	private CodeView(final String pathId) {
		this(pathId, "", "");
	}

	private CodeView(String pathId, String specification, String currentVersion) {
		this.pathId = pathId;
		this.specification = specification;
		this.currentVersion = currentVersion;
	}

	public String pathId() {
		return pathId;
	}

	public String specification() {
		return specification;
	}

	public String currentVersion() {
		return currentVersion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + (pathId == null ? 0 : pathId.hashCode());
		result = prime * result + (specification == null ? 0 : specification.hashCode());
		result = prime * result + (currentVersion == null ? 0 : currentVersion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		return pathId.equals(((CodeView) other).pathId);
	}

	@Override
	public String toString() {
		return "CodeView [pathId=" + pathId + ", specification=" + specification + ", currentVersion=" + currentVersion + "]";
	}
}
