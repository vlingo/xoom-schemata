// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

public class CodeView extends View {
	// reference in the form of 'org:unit:context:schema:version'
	private final String reference;

	private final SchemaVersionView schemaVersionView;

	public static CodeView empty() {
		return new CodeView();
	}

	public static CodeView with(final String reference) {
		return new CodeView(reference);
	}

	public static CodeView with(final String reference, SchemaVersionView schemaVersionView) {
		return new CodeView(reference, schemaVersionView);
	}

	private CodeView() {
		this("", SchemaVersionView.empty());
	}

	private CodeView(final String reference) {
		this(reference, SchemaVersionView.empty());
	}

	private CodeView(String reference, SchemaVersionView schemaVersionView) {
		this.reference = reference;
		this.schemaVersionView = schemaVersionView;
	}

	public SchemaVersionView schemaVersionView() {
		return schemaVersionView;
	}

	public String reference() {
		return reference;
	}

	public String status() {
		return schemaVersionView.status();
	}

	public String specification() {
		return schemaVersionView.specification();
	}

	public String currentVersion() {
		return schemaVersionView.currentVersion();
	}

	@Override
	public int hashCode() {
		final int prime = 89;
		int result = 1;

		result = prime * result + (reference == null ? 0 : reference.hashCode());
		result = prime * result + (schemaVersionView == null ? 0 : schemaVersionView.hashCode());
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

		return reference.equals(((CodeView) other).reference);
	}

	public CodeView mergeStatusWith(String reference, String status) {
		if (this.reference.equals(reference)) {
			SchemaVersionView merged = schemaVersionView.mergeStatusWith(schemaVersionView.schemaVersionId(), status);
			return new CodeView(this.reference, merged);
		} else {
			return this;
		}
	}

	@Override
	public String toString() {
		return "CodeView [reference=" + reference + ", status=" + schemaVersionView.status() + ", specification=" + schemaVersionView.specification()
				+ ", currentVersion=" + schemaVersionView.currentVersion() + "]";
	}
}
