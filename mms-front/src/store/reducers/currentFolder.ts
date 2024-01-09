import { UPDATE_CURRENT_FOLDER } from '@/store/constants';
import CurrentFolderState from '@/store/types/CurrentFolderState';

const initialState: CurrentFolderState = {
  data: {
    name: '/',
    id: '0',
  },
};

export default function currentFolder(state = initialState, action) {
  switch (action.type) {
    case UPDATE_CURRENT_FOLDER: {
      const { data } = action.payload;
      return {
        ...state,
        data,
      };
    }
    default:
      return state;
  }
}
