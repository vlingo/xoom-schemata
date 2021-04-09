// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NamedSchemaView {
	// reference in the form of 'org:unit:context:schema'
	private final String reference;

	private final SchemaView schemaView;
	private final List<SchemaVersionView> schemaVersions;

	public static NamedSchemaView empty() {
		return new NamedSchemaView();
	}

	public static NamedSchemaView with(String reference) {
		return new NamedSchemaView(reference);
	}

	public static NamedSchemaView with(String reference, SchemaView schemaView) {
		return new NamedSchemaView(reference, schemaView, new ArrayList<>());
	}

	private NamedSchemaView() {
		this("");
	}

	private NamedSchemaView(String reference) {
		this(reference, SchemaView.empty(), new ArrayList<>());
	}

	private NamedSchemaView(String reference, SchemaView schemaView, final List<SchemaVersionView> schemaVersions) {
		this.reference = reference;
		this.schemaView = schemaView;
		this.schemaVersions = schemaVersions;
	}

	public String organizationId() {
		return schemaView.organizationId();
	}

	public String unitId() {
		return schemaView.unitId();
	}

	public String contextId() {
		return schemaView.contextId();
	}

	public String schemaId() {
		return schemaView.schemaId();
	}

	public Category category() {
		return schemaView.category();
	}

	public Scope scope() {
		return schemaView.scope();
	}

	public String name() {
		return schemaView.name();
	}

	public String description() {
		return schemaView.description();
	}

	public SchemaVersionView greatestVersion() {
		return schemaVersions.stream()
				.max(SchemaVersionView::compareWith)
				.orElse(SchemaVersionView.empty());
	}

	public SchemaVersionView versionOf(String version) {
		return schemaVersions.stream()
				.filter(view -> view.hasCurrentVersion(version))
				.findAny()
				.orElse(SchemaVersionView.empty());
	}

	public List<SchemaVersionView> schemaVersions() {
		return Collections.unmodifiableList(schemaVersions);
	}

	public NamedSchemaView addSchemaVersion(SchemaVersionView view) {
		if (schemaVersions.contains(view)) {
			return this;
		} else {
			NamedSchemaView result = new NamedSchemaView(reference, schemaView, new ArrayList<>(schemaVersions));
			result.schemaVersions.add(view);

			return result;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [reference=" + reference + ", " + schemaView.toString() + ", schemaVersions: " + schemaVersions + "]";
	}
}
