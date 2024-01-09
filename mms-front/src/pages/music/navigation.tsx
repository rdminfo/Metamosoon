import React, { useEffect, useMemo, useState } from 'react';
import cardStyles from './style/card.module.less';
import styles from './style/navigation.module.less';
import locale from './locale';
import globalLocale from '@/locale';
import {
  Card,
  Skeleton,
  Space,
  Tree,
  Typography,
  Input,
} from '@arco-design/web-react';
import { NodeProps } from '@arco-design/web-react/lib/Tree/interface';
import { IconFolder, IconSearch } from '@arco-design/web-react/icon';
import useLocale from '@/utils/useLocale';
import Button from '@arco-design/web-react/lib/Button';
import useSWR from 'swr';
import store from '@/store';
import { FolderTreeList } from '@/pages/music/types/FolderTreeList';
import { FolderTreeListItem } from '@/pages/music/types/FolderTreeListItem';
import { NodeInstance } from '@arco-design/web-react/es/Tree/interface';
import { UPDATE_CURRENT_FOLDER } from '@/store/constants';
import { fetchFolderTreeList } from '@/service/music';
import {useSelector} from "react-redux";
import {RootState} from "@/store/types/RootState";

const generatorTreeNodes = (treeData) => {
  if (!treeData) return;
  return treeData.map((item) => {
    const { children, folderId, folderName } = item;
    return (
      <Tree.Node
        key={folderId}
        title={folderName}
        dataRef={item}
        className={styles['tree-node-span-hover']}
      >
        {children ? generatorTreeNodes(item.children) : null}
      </Tree.Node>
    );
  });
};

const Navigation = (props) => {
  const { height } = props;
  const t = useLocale(locale);
  const tg = useLocale(globalLocale);
  const { data, isLoading } = useSWR<{ data: FolderTreeList }>(
    'fetchFolderTreeList',
    fetchFolderTreeList
  );

  const { currenFolder } = useSelector((state: RootState) => ({
    currenFolder: state.currentFolder.data,
  }));

  const allExpandedKeys = useMemo(() => data?.data?.folderIds, [data]);
  const treeDataOrigin = useMemo(() => data?.data?.treeData, [data]);

  const [selectedKeys, setSelectedKeys] = useState([]);
  const [expandedKeys, setExpandedKeys] = useState([]);
  const [treeSearch, setTreeSearch] = useState(false);
  const [searchStr, setSearchStr] = useState('');
  const [treeData, setTreeData] = useState<FolderTreeListItem[]>();

  useEffect(() => {
    setTreeData(
      isLoading ? [] : searchStr ? searchData(searchStr) : treeDataOrigin
    );
  }, [searchStr, treeDataOrigin]);

  useEffect(() => {
    setSelectedKeys([currenFolder.id])
    if (!expandedKeys.includes(currenFolder.id)) {
      setExpandedKeys(prevExpandedKeys => [...prevExpandedKeys, currenFolder.id]);
    }
  }, [currenFolder]);

  function searchData(inputValue) {
    const loop = (data) => {
      const result: FolderTreeListItem[] = [];
      data.forEach((item) => {
        if (
          item.folderName.toLowerCase().indexOf(inputValue.toLowerCase()) > -1
        ) {
          result.push({ ...item });
        } else if (item.children) {
          const filterData = loop(item.children);
          if (filterData.length) {
            result.push({ ...item, children: filterData });
          }
        }
      });
      return result;
    };
    return loop(treeDataOrigin);
  }

  function treeSelect(value, info: NodeInstance) {
    const folderId = value?.length > 0 ? value?.[0] : 0;
    const folderName = info?.props?.title;
    store.dispatch({
      type: UPDATE_CURRENT_FOLDER,
      payload: { data: { id: folderId, name: folderName } },
    });
  }

  return (
    <Card className={styles.navigation} style={{ height: height }}>
      <Space
        className={`${cardStyles['nav-bar']} ${styles['navigation-nav']}`}
        style={{ minHeight: '28px' }}
      >
        {treeSearch ? (
          <Input.Search
            onChange={(value) => {
              setSearchStr(value);
            }}
            size={'small'}
          />
        ) : (
          <Space>
            <Typography.Title
              className={cardStyles['nav-bar-title']}
              heading={6}
            >
              {t['music.navigation.nav.title']}
            </Typography.Title>
            <Button
              className={cardStyles['nav-bar-button']}
              type={'text'}
              size={'mini'}
              disabled={isLoading}
              onClick={() => setTreeSearch(true)}
              icon={<IconSearch />}
            />
          </Space>
        )}
        {treeSearch ? (
          <Button
            className={cardStyles['nav-bar-button']}
            type="text"
            size={'mini'}
            disabled={isLoading}
            onClick={() => {
              setTreeSearch(false);
              setSearchStr('');
              setTreeData(treeDataOrigin);
            }}
          >
            {tg['ope.cancel']}
          </Button>
        ) : (
          <Button.Group>
            <Button
              className={cardStyles['nav-bar-button']}
              type="text"
              size={'mini'}
              disabled={isLoading}
              onClick={() => setExpandedKeys(allExpandedKeys)}
            >
              {tg['ope.expand']}
            </Button>
            <Button
              className={cardStyles['nav-bar-button']}
              type="text"
              size={'mini'}
              disabled={isLoading}
              onClick={() => setExpandedKeys([])}
            >
              {tg['ope.merge']}
            </Button>
          </Button.Group>
        )}
      </Space>
      <div
        className={styles['tree-outer']}
        style={{ height: height - 20 * 2 - 24 }}
      >
        {isLoading ? (
          <Skeleton text={{ rows: 6 }} style={{ paddingRight: '20px' }} />
        ) : (
          <Tree
            className={styles['tree']}
            selectedKeys={selectedKeys}
            expandedKeys={expandedKeys}
            onSelect={(value, info) => {
              setSelectedKeys(value)
              treeSelect(value, info.node);
            }}
            onExpand={(keys) => {
              setExpandedKeys(keys);
            }}
            size={'small'}
            blockNode
            renderTitle={(node: NodeProps) => {
              return (
                <div className={styles['tree-node-wrapper']}>
                  <IconFolder style={{ fontSize: 18 }} />
                  <span className={styles['tree-node-span']}>{node.title}</span>
                </div>
              );
            }}
          >
            {generatorTreeNodes(treeData)}
          </Tree>
        )}
      </div>
    </Card>
  );
};

export default Navigation;
