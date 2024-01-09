import React, {useContext, useEffect, useMemo, useState} from 'react';
import { useHistory } from 'react-router-dom';
import {
  Tooltip,
  Select,
  Menu,
  Message, Space,
} from '@arco-design/web-react';
import {
  IconLanguage,
  IconNotification,
  IconSunFill,
  IconMoonFill,
} from '@arco-design/web-react/icon';
import { GlobalContext } from '@/context';
import useLocale from '@/utils/useLocale';
import MessageBox from '@/components/MessageBox';
import IconButton from './IconButton';
import styles from './style/index.module.less';
import defaultLocale from '@/locale';
import {getDefaultRoutes, getRoutes, routeMap} from "@/routes";
import NProgress from 'nprogress';


function Navbar() {

  const routes = useMemo(() => getRoutes() || [], []);

  const [selectedKeys, setSelectedKeys] = useState<string[]>([getDefaultRoutes()]);

  const { setLang, lang, theme, setTheme } = useContext(GlobalContext);

  const t = useLocale();

  const history = useHistory();

  const pathname = history.location.pathname;

  function onClickMenuItem(key) {
    const currentRoute = routes.find((r) => r.key === key);
    const component = currentRoute.component;
    const preload = component.preload();
    NProgress.start();
    preload.then(() => {
      setSelectedKeys(key);
      history.push(currentRoute.path ? currentRoute.path : `/${key}`);
      NProgress.done();
    });
  }

  function updateMenuStatus() {
    const pathKeys = pathname.split('/');
    const newSelectedKeys: string[] = [];
    while (pathKeys.length > 0) {
      const currentRouteKey = pathKeys.join('/');
      const menuKey = currentRouteKey.replace(/^\//, '');
      if (routeMap().has(menuKey)) {
        newSelectedKeys.push(menuKey);
      }
      pathKeys.pop();
    }
    setSelectedKeys(newSelectedKeys);
  }

  useEffect(() => {
    updateMenuStatus();
  }, [pathname]);


  return (
    <div className={styles.navbar}>
      <div className={styles.left}>
        <Menu mode='horizontal' selectedKeys={selectedKeys} onClickMenuItem={onClickMenuItem}>
          <Menu.Item key='home' style={{marginLeft: '-10px'}}>{t['navBar.home']}</Menu.Item>
          <Menu.Item key='music'>{t['navBar.music']}</Menu.Item>
          <Menu.Item key='movie'>{t['navBar.movie']}</Menu.Item>
          <Menu.Item key='setting'>{t['navBar.setting']}</Menu.Item>
        </Menu>
      </div>
      <Space size={"medium"} className={styles.right}>
        <MessageBox>
          <IconButton icon={<IconNotification />} />
        </MessageBox>
        <Select
          triggerElement={<IconButton icon={<IconLanguage />} />}
          options={[
            { label: '中文', value: 'zh-CN' },
            { label: 'English', value: 'en-US' },
          ]}
          value={lang}
          triggerProps={{
            autoAlignPopupWidth: false,
            autoAlignPopupMinWidth: true,
            position: 'br',
          }}
          trigger="hover"
          onChange={(value) => {
            setLang(value);
            const nextLang = defaultLocale[value];
            Message.info(`${nextLang['message.lang.tips']}${value}`);
          }}
        />
        <Tooltip
          content={
            theme === 'light'
              ? t['settings.navbar.theme.toDark']
              : t['settings.navbar.theme.toLight']
          }
        >
          <IconButton
            icon={theme !== 'dark' ? <IconMoonFill /> : <IconSunFill />}
            onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
          />
        </Tooltip>
      </Space>
    </div>
  );
}

export default Navbar;
