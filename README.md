
<p align="center"><img width="10%" alt="스크린샷 2022-09-30 오전 9 31 29" src="https://user-images.githubusercontent.com/72914519/193180901-160ceff5-5729-427f-997b-73f14cf4b611.png"></p>

<h3 align="center"> ZZANDI </h3>

  <p align="center">
    공부 자극을 위한 스터디 참여 & 진도 공유 사이트 입니다.
    <br />
    <a href="http://www.zzandi.ay.ms/"><strong>사이트이동</strong></a>
    <br />
    <a href="https://www.youtube.com/watch?v=Uq0LaeyqQoE"><strong>기능 영상</strong></a>
    <br />

  </p>
</div>

-----

### 🙋‍♂️ 팀원



------
### ⭐️ 프로젝트 목표

- 타임리프를 활용하여 페이지 제작
- NCP를 사용한 배포 및 운영
- CI/CD 자동화 구축

------

### 🛠 사용기술
<p align="center"><img width="60%" alt="스크린샷 2022-09-30 오전 9 31 29" src="https://user-images.githubusercontent.com/72914519/193177572-328f6557-56ae-4393-9692-ab2f66fa078d.png"></p>

------

### 🧰 주요기능
------
## 사용자
- 유저는 회원가입을 할수 있다.
- 유저는 로그인을 할수 있다.
- 유저는 닉네임을 변경할수 있다.
- 유저는 프로필을 변경할수 있다.
- 유저는 비밀번호를 변경할수 있다.
- 회원가입시 이메일 인증을 해야한다.
- 회원 탈퇴를 할수 있다.

## 스터디
- 이메일 인증된 회원만 스터디를 서비스를 이용할수 있다.
- 스터디 등록을 한 유저는 스터디 장이 된다.
- 스터디장이 스터디 탈퇴시 스터디원에게 권한을 위임해야한다.
- 스터디 등록시 책 or 강의를 입력해야하다. 책은 알라딘 api를 사용한다.
- 스터디 참가시 스터디장의 승인이 필요한다

## 게시판
- 스터디와 게시판은 1:1관계이다.
- 게시판에는 카테고리가 있어야한다.




### 🎊 Architecture & Pipeline

<p align="center"><img width="60%" alt="스크린샷 2022-09-30 오전 9 31 29" src="https://user-images.githubusercontent.com/72914519/193177661-cb216598-656c-48e7-ae87-92bfea0957d1.png"></p>

-----

### 🔑**CI & CD Pipeline**

- Develop 브랜치 기능 개발 완료 후, Main 브랜치에 Pull Request 전송
- Main 브랜치 PR 이후, Merge and Push
- Jenkins 동작
- Main 브랜치 빌드
- 빌드 결과물 Jar 파일 -> Docker 빌드 -> 도커 이미지 생성
- 도커 이미지 -> 도커 컨테이너 실행

------

### 🏠 ERD 구조
<p align="center"><img width="50%" alt="스크린샷 2022-09-30 오전 9 31 29" src="https://user-images.githubusercontent.com/72914519/193180036-6a591fc1-eea1-49f6-a7a5-a2855c08e6e5.png"></p>

------



