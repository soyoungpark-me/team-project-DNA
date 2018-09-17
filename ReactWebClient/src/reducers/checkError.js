import config from './../config';

const checkError = (action) => {
  const USER_API = `${config.SERVER_HOST}:${config.USER_PORT}/api`;
  // 일단 에러가 참일 경우
  if (action.error && action.payload) {
    const response = action.payload.response;

    // 네트워크 에러 (서버가 죽은 경우)
    if (!response) {
        alert("서버가 죽었습니다.");
        return;
    }

    // 서버 오류
    if (response.status >= 500) {
      alert("500 internal server error");
      return;
    }

    // 에러 코드가 400대 일 경우 (잘못된 요청)
    else if (response.status >= 400) {
      // 토큰 관련 에러일 경우
      // 1. 토큰이 유효하지 않을 경우 가지고 있는 refresh 토큰으로 다시 요청해야 한다.
      if (response.data.code === 11400 || response.data.code === 12400) {
        fetch(`${USER_API}/auth/refresh`, {
          method: 'GET',
          headers: {
            token: JSON.parse(sessionStorage.getItem("token")).refreshToken
          }
        }).then((result) => {
          return result.json()
        }).then(function(response) {
          // 2. 만약 refresh 토큰마저 유효하지 않으면
          // 모든 토큰을 삭제하고 다시 로그인을 해야 한다.
          sessionStorage.removeItem("token");
        });
      }
    }
  }

  return action;
}

export default checkError;
