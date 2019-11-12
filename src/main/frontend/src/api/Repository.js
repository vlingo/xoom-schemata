import axios from 'axios'
import store from '@/store';


let client = axios.create({
  baseURL: '/',
})

client.interceptors.request.use(function (config) {
  store.commit('startLoading');
  return config
})

client.interceptors.response.use(function (response) {
  store.commit('finishLoading');
  return response
})

export default client
