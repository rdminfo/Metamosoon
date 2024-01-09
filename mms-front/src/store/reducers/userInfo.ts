import { UPDATE_USER_INFO } from '@/store/constants';
import UserInfoState from '@/store/types/UserInfoState';

const initialState: UserInfoState = {
  data: {
    permissions: {},
  },
};

export default function userInfo(state = initialState, action) {
  switch (action.type) {
    case UPDATE_USER_INFO: {
      const { data = initialState.data, userLoading } = action.payload;
      return {
        ...state,
        userLoading,
        data,
      };
    }
    default:
      return state;
  }
}
