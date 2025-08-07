# 실시간 협업 노트
> Y.js 기반의 실시간 문서 협업 시스템

---

## 1. 설명
이 프로젝트는 여러 사용자가 동시에 하나의 문서를 실시간으로 작성하고 편집할 수 있는 협업 노트 시스템입니다.
충돌 없는 데이터 동기화를 위해 CRDT 기반의 Y.js를 사용하며, WebSocket을 통해 실시간 연결을 유지합니다.
또한, Redis를 통해 세션 및 임시 데이터를 관리하고, DB에 최종 문서를 저장합니다.

---

## 2. 구조
Frontend (React + Y.js)

  ↕ WebSocket (SockJS)
  
Spring Boot WebSocket Server (동기화 메시지 중계 및 관리)

  ↕
  
Redis (세션 관리, 임시 문서 상태 캐싱)

  ↕
  
DB (문서 저장 - MongoDb, MySQL ... )

---

## 3. 주요 기능

### 🔹 Frontend (React + Y.js)
- 사용자는 React 기반 UI를 통해 문서를 작성합니다.
- Y.js를 통해 로컬에서 발생한 변경 사항은 자동으로 CRDT 방식으로 병합되며,
- WebSocket(SockJS)을 통해 서버와 실시간으로 동기화됩니다.

### 🔹 Spring Boot WebSocket Server
- 클라이언트 간 실시간 동기화를 위한 WebSocket 서버입니다.
- Y.js 업데이트 메시지를 받아 다른 사용자에게 브로드캐스트합니다.
- 문서 상태를 Redis에 저장하거나, 주기적으로 DB에 반영합니다.

### 🔹 Redis
- 실시간 성능을 위해 문서 상태를 캐싱합니다.
- 사용자 접속 상태 및 세션 정보를 임시로 저장합니다.
- Redis Pub/Sub을 활용해 여러 서버 간 메시지를 동기화할 수 있습니다.

### 🔹 DB (MongoDB, MySQL 등)
- 최종 문서 데이터를 영구적으로 저장합니다.
- 세션 종료 시 또는 일정 주기마다 Redis의 데이터를 DB에 반영합니다.
- 문서 복구 및 히스토리 관리 기능을 위한 기반을 제공합니다.
