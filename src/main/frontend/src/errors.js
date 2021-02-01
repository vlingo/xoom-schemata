const errors = Object.freeze({
	EMPTY: "May not be empty",
	VERSION: "Must be a semantic version number (major.minor.patch, e.g. 1.6.12)",
	CLASSNAME: "Must be initial cap (e.g. SomethingDefined)",
	NAMESPACE: "Must be namespace syntax (e.g. com.example.demo)"
});

export default errors