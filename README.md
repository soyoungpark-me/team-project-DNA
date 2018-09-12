# DNA (Dear, Nearby Anyone)

* 2018년 2학기의 졸업작품 프로젝트를 위한 저장소입니다.

* README에 개발하면서 힘들었던 점, 깊게 고민이 필요했던 점 등에 대해 **일지 형식**으로 기록하고 있습니다.

  ​

***

## 프로젝트 소개

#### **"특별한 이벤트가 일어나는 공간에 있는 사람만 느낄 수 있는 감정과 생각을, 부담없이 공유할 수 있는 서비스를 만들어 보고 싶습니다.**"

![소개 이미지](https://blogfiles.pstatic.net/MjAxODA4MTRfMTky/MDAxNTM0MTc3NzY1MTM0.NCF4J6dMHOkkdIOZfhedD3jE3ClrDsU62BCsdAeryVog.SYjZhtyBsQAmZE8KFL6X0tSj9-sHzwbr3qhSbeTFfWYg.PNG.3457soso/title.png)

- 초기 기획서 : [Require_Analysis_DNA_project.pdf](https://github.com/3457soso/project_DNA/blob/master/DNA_project.pdf) (만들어준 팀원들 고마워요!)

- 실시간이면서 다량의 요청이 들어오는 서비스를 개발해 보고 싶었고, 만들고 직접 써보기에 재미있는 주제를 정해 최대한 완성도를 높여서 완성하고, 실제로 **배포까지** 해보는 것을 목표로 주제를 잡았습니다.

- 현 위치를 기준으로 **지정한 반경 내에서 작성된 채팅만 보여주는** 서비스로, 이 외에도 포스팅, 친구, DM 등 다양한 기능들이 기획되어 있습니다.

  

***

## 프로젝트 아키텍처 구조도

#### 2018.09.01 : 성능 개선 전
![아키텍처 구조도](https://github.com/3457soso/team-project-DNA/blob/master/Resource/Arcitecture_Design_0901.png)


#### 2018.09.10 : AWS lambda 추가
![아키텍처 구조1](https://github.com/3457soso/team-project-DNA/blob/master/Resource/Arcitecture_Design_0910.png)
주요 기능은 모두 개발이 완료 된 상태의 구조도입니다.

추후 성능 상의 개선점이 생겨 아키텍처가 변경되면 해당 내용도 추가해 다시 업로드할 예정입니다!



***

## 개발 일지

최대한 해당 부분의 개발이 끝나는 즉시 바로바로 작성하려 노력하고 있지만... 

팀 프로젝트여서 시간에 쫓기며 개발하다 보니 조금씩 밀리는 경우도 있습니다!



| 번호 |    날짜    | 내용 (링크)                                                  |
| :--: | :--------: | ------------------------------------------------------------ |
|  01  | 2018.08.01 | [**JWT로 인증 서버 구현하기**](https://github.com/3457soso/project_DNA/blob/master/devLog/2018.08.01%20:%20JWT%EB%A1%9C%20%EC%9D%B8%EC%A6%9D%20%EC%84%9C%EB%B2%84%20%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0.md) |
|  02  | 2018.08.06 | [**Jenkins, Docker로 지속적 배포(CD) 도전하기**](https://github.com/3457soso/project_DNA/blob/master/devLog/2018.08.06%20:%20Jenkins%2C%20Docker%EB%A1%9C%20%EC%A7%80%EC%86%8D%EC%A0%81%20%EB%B0%B0%ED%8F%AC(CD)%20%EB%8F%84%EC%A0%84%ED%95%98%EA%B8%B0.md) |
|  03  | 2018.08.08 | [**MongoDB와 Mongoose를 이용한 위치 기반 서비스 개발 (작성중)**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.08.08%20:%20MongoDB%EC%99%80%20Mongoose%EB%A5%BC%20%EC%9D%B4%EC%9A%A9%ED%95%9C%20%EC%9C%84%EC%B9%98%20%EA%B8%B0%EB%B0%98%20%EC%84%9C%EB%B9%84%EC%8A%A4%20%EA%B0%9C%EB%B0%9C.md) |
|  04  | 2018.08.09 | [**MongoDB에 AWS ElasticSearch, Kibana 연동하기**](https://github.com/3457soso/project_DNA/blob/master/devLog/2018.08.09%20:%20MongoDB%EC%97%90%20ElasticSearch%2C%20Kibana%20%EC%97%B0%EB%8F%99%ED%95%98%EA%B8%B0.md) |
|  05  | 2018.08.13 | [**https 환경 구축하기**](https://github.com/3457soso/project_DNA/blob/master/devLog/2018.08.13%20:%20SSL%EC%9D%84%20%EC%9D%B4%EC%9A%A9%ED%95%B4%20https%20%ED%99%98%EA%B2%BD%20%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0.md) |
|  06  | 2018.08.15 | [**라즈베리파이로 서버 구축하기**](https://github.com/3457soso/project_DNA/blob/master/devLog/2018.08.15%20:%20%EB%9D%BC%EC%A6%88%EB%B2%A0%EB%A6%AC%ED%8C%8C%EC%9D%B4%EB%A1%9C%20%EC%84%9C%EB%B2%84%20%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0.md) |
|  07  | 2018.08.20 | [**리액트로 웹 프론트엔드 구현하기**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.08.20%20:%20%EB%A6%AC%EC%95%A1%ED%8A%B8%EB%A1%9C%20%EC%9B%B9%20%ED%94%84%EB%A1%A0%ED%8A%B8%EC%97%94%EB%93%9C%20%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0.md) |
|  08  | 2018.08.22 | [**실시간 서비스를 위해 socket.io 이용하기**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.08.22%20:%20%EC%8B%A4%EC%8B%9C%EA%B0%84%20%EC%84%9C%EB%B9%84%EC%8A%A4%EB%A5%BC%20%EC%9C%84%ED%95%B4%20Socket.io%20%EC%9D%B4%EC%9A%A9%ED%95%98%EA%B8%B0.md) |
|  09  | 2018.08.24 | [**Redis의 Geo API 사용하기 (작성중)**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.08.24%20:%20Redis%EC%9D%98%20Geo%20API%20%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0.md) |
|  10  | 2018.08.28 | [**배포와 함께 CORS 문제 극복하기**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.08.28%20:%20%EB%B0%B0%ED%8F%AC%EC%99%80%20%ED%95%A8%EA%BB%98%20CORS%20%EB%AC%B8%EC%A0%9C%20%EA%B7%B9%EB%B3%B5%ED%95%98%EA%B8%B0.md) |
|  11  | 2018.08.31 | [**주요 기능의 백엔드 개발을 끝마친 후기**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.08.31%20:%20%EC%A3%BC%EC%9A%94%20%EA%B8%B0%EB%8A%A5%EC%9D%98%20%EB%B0%B1%EC%97%94%EB%93%9C%20%EA%B0%9C%EB%B0%9C%EC%9D%84%20%EB%81%9D%EB%A7%88%EC%B9%9C%20%ED%9B%84%EA%B8%B0.md) |
|  12  | 2018.09.01 | [**채팅 서버의 성능 개선을 위한 부하 테스트**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.09.01%20:%20%EC%B1%84%ED%8C%85%20%EC%84%9C%EB%B2%84%EC%9D%98%20%EC%84%B1%EB%8A%A5%20%EA%B0%9C%EC%84%A0%EC%9D%84%20%EC%9C%84%ED%95%9C%20%EB%B6%80%ED%95%98%20%ED%85%8C%EC%8A%A4%ED%8A%B8.md) |
|  13  | 2018.09.10 | [**성능 개선 1) AWS Lambda를 이용한 서버리스 이미지 리사이징**](https://github.com/3457soso/team-project-DNA/blob/master/devLog/2018.09.10%20:%20AWS%20Lambda%EB%A5%BC%20%EC%9D%B4%EC%9A%A9%ED%95%9C%20%EC%9D%B4%EB%AF%B8%EC%A7%80%20%EB%A6%AC%EC%82%AC%EC%9D%B4%EC%A7%95.md) |
|  14  | 2018.09.12 | [**성능 개선 2) RabbitMQ를 통해 소켓 서버 기능 분할하기**]() |



#### 

**... 진행 중 ...**
