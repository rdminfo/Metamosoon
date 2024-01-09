import axios from 'axios';
import {Message} from "@arco-design/web-react";

export function createUrl(uri: string) {
  // return 'http://172.16.0.249:58080/api' + uri;
  return 'http://localhost:58080/api' + uri;
}

const headers = {
  'Content-Type': 'application/json',
};

const api = axios.create({ headers });

export const get = (url: string, params?) =>
  api.get(url, { params }).then((res) => res.data);

export const post = (url: string, data?) =>
  api.post(url, data).then((res) => res.data);

export const del = (url: string) => api.delete(url).then((res) => res.data);

export const put = (url: string, data?) =>
  api.put(url, data).then((res) => res.data);

api.interceptors.response.use(
  (response) => {
    const successResponse = response && response.status && 200 === response.status;
    if (successResponse && response.data) {
      const data = response.data
      const apiError = !data.success && 1000 !== data.code
      if (apiError) {
        Message.error(data.error || data.message)
      }
    }
    return response
  },
  (error) => {
    console.log(error)
    const err = error?.response?.data;
    throw new Error({
      message: err?.message ?? error?.response?.data?.message ?? '',
      errorCode: err?.errorCode ?? error?.response?.data?.code ?? -1,
      httpStatusCode:
        error?.response?.httpStatusCode ?? error.response?.status ?? -1,
      method: err?.config?.method ?? error.config?.method,
      path: err?.config?.url ?? error.config?.url,
    });
  }
);

class Error {
  message: string;
  errorCode: number;
  httpStatusCode: number;
  path: string;
  method: string;

  constructor({
    errorCode,
    httpStatusCode,
    message,
    path,
    method,
  }: {
    message: string;
    errorCode: number;
    httpStatusCode: number;
    path: string;
    method: string;
  }) {
    this.message = message;
    this.errorCode = errorCode;
    this.httpStatusCode = httpStatusCode;
    this.path = path;
    this.method = method;
  }

  toString() {
    return JSON.stringify(this);
  }
}

export default api;
