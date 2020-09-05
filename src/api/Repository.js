
const root = "http://localhost:9019"

export async function get(path) {
	const res = await fetch(root + path);
	return res;
}

export async function post(path, body) {
	const res = await fetch(root + path, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json'},
		body: JSON.stringify(body)
	});
	return res;
}

let client = { get, post }

export default client;
  