# CONMOTO
https://conmoto.site
## 배경
구글 I/O 2022를 구경하던 중 Flutter 업데이트 뉴스에서
Notion같은 워크스페이스를 제공하는 'Superlist' 제품을 알게됐습니다.

https://youtu.be/w_ezWG1yKQQ?t=357

기존 서비스에 아쉬움을 느끼고 있던 찰나에 해당 제품을 보고나니 이러한 서비스들이 어떻게 제작되는지 궁금해졌습니다.<br/>
따라서 기존 서비스들을 가볍게 클론하면서 단점들을 보완한 서비스를 개발하는 토이프로젝트를 시작하게 됐습니다.

[기존 사용했던 서비스]

1. Notion의 경우 다양한 협업 기능 및 템플릿을 제공하지만, 에디터 기능은 많이 부실했다.<br/> (특정 이미지 사이즈만 제공, 제한된 글자 폰트/크기 등)<br/>
2. Superlist의 경우 작업위치 공유기능이 가장 인상깊었다.<br/> 하지만, To-Do List의 확장 버전으로 무언가 상세한 내용을 공유하기엔 다소 부적합해보였다.<br/>
3. Tistory의 경우 다양한 에디터를 제공한다.(위지윅, 마크다운, HTML)<br/> 커스터마이징 제약이 거의 없다싶을 만큼 자유롭지만, 블로그 특성상 협업기능은 존재하지 않는다.

## 목표
1. 기능 구현에만 치중하는 것이 아니라 신중한 기술선택, 설계, 클린 코드에도 신경쓴다.
2. TDD 원칙을 따르려 노력한다.
3. 대용량 트래픽 처리까지 고려한 기능을 구현한다.

## API DOCS
https://documenter.getpostman.com/view/11771159/2s8ZDcyKfd

## ERD
### RDBMS (MySQL)
![RDBMS-ERD](https://user-images.githubusercontent.com/66458794/215076866-d0afdfcf-c74a-4518-a5a8-9922f6ec9952.png)
### NoSQL (MongoDB)
![NoSQL-ERD](https://user-images.githubusercontent.com/66458794/215081538-428d9c66-16e0-4ba6-969e-987f4737282a.png)


## 아키텍처
### (2023.01.27 기준)
![conmoto-2023-01-27](https://user-images.githubusercontent.com/66458794/215075626-6d5b21ca-e537-44d0-8ade-fd19248aa7bd.png)

## TOOL
Skill Name | Version
---|---
Spring Boot | 2.7.0
Thymeleaf-layout | 3.0.0
BootStrap | 4.4.1
jQuery | 3.4.1
jQuery-UI | 1.13.2
Gradle | 7.4.1
h2 | 1.4.200
mysql | 8.0.29
MongoDB | 6.0.3
ToastUI | 3.2.1
