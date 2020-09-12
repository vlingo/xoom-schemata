const errors = Object.freeze({
	EMPTY: "may not be empty",
	VERSION: "must be a semantic version number (major.minor.patch, e.g. 1.6.12)",
	SUBMIT: "fields may not be empty",
	SUBMITVER: "previous and current version must be a semantic version number (major.minor.patch, e.g. 1.6.12)"
});

export default errors