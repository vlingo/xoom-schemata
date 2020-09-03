export async function post(req, res, next) {
	/* Initializes */
	res.setHeader('Content-Type', 'application/json')
	/* Retrieves the data */
	var data = req.body
	// Do something with the data...
	/* Returns the result */
	return res.end(JSON.stringify({ success: true }))
}