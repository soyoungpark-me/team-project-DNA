export const FETCH_DATA_SUCCESS = 'FETCH_DATA_SUCCESS';
export const FETCH_BEST_SUCCESS = 'FETCH_BEST_SUCCESS';

export function fetchDataSuccess(data) {
  return {
    type: FETCH_DATA_SUCCESS,
    payload: data
  };
}

export function fetchBestSuccess(data) {
  return {
    type: FETCH_BEST_SUCCESS,
    payload: data
  };
}
