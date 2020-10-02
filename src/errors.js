const errors = Object.freeze({
	EMPTY: "may not be empty",
	VERSION: "must be a semantic version number (major.minor.patch, e.g. 1.6.12)",
	CLASSNAME: "must be a java class name (first letter, e.g. SomethingDefined)",
	SUBMIT: "fields may not be empty",
	SUBMITVER: "previous and current version must be a semantic version number (major.minor.patch, e.g. 1.6.12)"
});

export default errors