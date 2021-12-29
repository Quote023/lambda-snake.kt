if (config.devServer) {
    config.devServer = {
        ...config.devServer,
        compress: true,
        port: '8080',
        allowedHosts: [
            '.gitpod.io'
        ]
    }
}