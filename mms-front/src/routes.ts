import {AuthParams} from '@/utils/authentication';
import lazyload from './utils/lazyload';

export type IRoute = AuthParams & {
  name: string;
  key: string;
  // 当前页是否展示面包屑
  breadcrumb?: boolean;
  children?: IRoute[];
  // 当前路由是否渲染菜单项，为 true 的话不会在菜单中显示，但可通过路由地址访问。
  ignore?: boolean;
};

const routes: IRoute[] = [
  {
    name: 'home',
    key: 'home',
  },
  {
    name: 'music',
    key: 'music',
  },
  {
    name: 'movie',
    key: 'movie',
  },
  {
    name: 'setting',
    key: 'setting',
  },
];

export const routeMap = (): Map<string, string> => {
  const routerMap = new Map;
  routes.map((route) => {
    routerMap.set(route.key, '');
  });
  return routerMap;
}
export function getRoutes() {
  const res = [];
  function travel(_routes) {
    _routes.forEach((route) => {
      const visibleChildren = (route.children || []).filter(
          (child) => !child.ignore
      );
      if (route.key && (!route.children || !visibleChildren.length)) {
        try {
          route.component = lazyload(() => import(`./pages/${route.key}`));
          res.push(route);
        } catch (e) {
          console.error(e);
        }
      }
      if (route.children && route.children.length) {
        travel(route.children);
      }
    });
  }
  travel(routes);
  return res;
}

export function getDefaultRoutes() {
  const first = routes[0];
  if (first) {
    return first?.children?.[0]?.key || first.key;
  }
  return '';
}

export default routes;
