import React, { useMemo } from 'react';
import { Switch, Route } from 'react-router-dom';
import { Layout, Spin } from '@arco-design/web-react';
import cs from 'classnames';
import { useSelector } from 'react-redux';
import Navbar from './components/NavBar';
import Footer from './components/Footer';
import getUrlParams from './utils/getUrlParams';
import lazyload from './utils/lazyload';
import styles from './style/layout.module.less';
import { Redirect } from 'react-router';
import { getDefaultRoutes, getRoutes } from '@/routes';
import { RootState } from '@/store/types/RootState';

const Content = Layout.Content;

function PageLayout() {
  const urlParams = getUrlParams();
  const { settings, userLoading } = useSelector((state: RootState) => ({
    settings: state.settings.data,
    userLoading: state.userInfo.userLoading,
  }));
  const routes = useMemo(() => getRoutes() || [], []);
  const showNavbar = settings.navbar && urlParams.navbar !== false;
  const showFooter = settings.footer && urlParams.footer !== false;

  return (
    <Layout className={styles.layout}>
      <div
        className={cs(styles['layout-navbar'], {
          [styles['layout-navbar-hidden']]: !showNavbar,
        })}
      >
        <Navbar />
      </div>
      {userLoading ? (
        <Spin className={styles['spin']} />
      ) : (
        <Layout>
          <Layout className={styles['layout-content']}>
            <div className={styles['layout-content-wrapper']}>
              <Content>
                <Switch>
                  {routes.map((route, index) => {
                    return (
                      <Route
                        key={index}
                        path={`/${route.key}`}
                        component={route.component}
                      />
                    );
                  })}
                  <Route exact path="/">
                    <Redirect to={getDefaultRoutes()} />
                  </Route>
                  <Route
                    path="*"
                    component={lazyload(() => import('./pages/exception/403'))}
                  />
                </Switch>
              </Content>
            </div>
            {showFooter && <Footer />}
          </Layout>
        </Layout>
      )}
    </Layout>
  );
}

export default PageLayout;
