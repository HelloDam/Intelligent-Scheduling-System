const token = uni.getStorageSync('user')
export default {
	common: {
		// baseUrl: 'http://localhost:6001',
		baseUrl: 'http://10.23.12.180:6001',
		data: {},
		header: {
			'token': token
		},
		method: "GET",
		dataTyoe: 'json'
	},
	request(options = {}) {
		options.url = this.common.baseUrl + options.url;
		options.data = options.data || this.common.data;
		options.header = options.header || this.common.header;
		options.method = options.method || this.common.method;
		options.dataTyoe = options.dataTyoe || this.common.dataTyoe;
		return new Promise((res, rej) => {
			uni.request({
				...options,
				success: (result) => {
					if (result.statusCode != 200) {
						return rej();
					}

					let data = result.data;
					res(data);
				}
			})
		})
	}
}