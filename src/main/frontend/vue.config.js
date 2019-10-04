module.exports = {
    publicPath: '/app/',
    devServer: {
        proxy: {
            '/organizations': {
                target: 'http://localhost:9019/'
            },
            '/schema': {
                target: 'http://localhost:9019/'
            },
            '/categories': {
                target: 'http://localhost:9019/'
            }
        },
    },
}