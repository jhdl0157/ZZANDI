'use strict'

const studyDetail_left = document.querySelector(".studyDetail-left");
const studyDetail_top = document.querySelector(".studyDetail-top");
const studyId = document.querySelector(".studyId").value;
const userNickname = document.querySelector(".userNickname").value;

window.onload = async () => {
  const teamMateList = await findTeamMateList(studyId, userNickname);
  checkTeamMate(teamMateList);
  findStudyDetail(studyId);
}

let isParticipation = false;
let isTeamMate = false;
let isDelete = false;
let cnt = 0;

function findStudyDetail(studyId) {
  console.log("findStudyDetail 실행");
  fetch(`/study/detail/${studyId}/study-data`)
  .then(response => response.json())
  .then(data => {
    displayStudy(data);
    dis(data);
  });
}

function displayStudy(data) {
  console.log("displayStudy 실행");
  // StudyDetail-left [start]
  let html = '';
  html += `
    <div>
      <p id = "studyTag"># ${data.studyTag}</p>
      <a href="/study/coverUpload/${studyId}">
        <img class="studyCover flex-shrink-0" src="${data.studyCoverUrl}">
      </a>
  `;

  console.log("isParticipation : " + isParticipation);
  // 팀원이 아니며 아직 모집 중일 때,
  if (!isParticipation && data.studyStatus == 'RECRUIT') {
    html += `
      <button onclick = "create()" class="participationbtn btn btn-outline-primary mt-3">참가 신청</button>
    `;
  }

  // 팀원이지만 팀장이 아니며, 아직 진행 중이 아닌 경우,
  if(isTeamMate && (userNickname != data.leader) && (data.studyStatus == 'RECRUIT' || data.studyStatus == 'RECRUIT_COMPLETE')){
    html += `
      <button onclick = "quit()" class="quitbtn btn btn-outline-secondary mt-3">탈퇴하기</button>
    `;
  }

  html += `</div>`
  studyDetail_left.innerHTML = html;
  // StudyDetail-left [end]

  // StudyDetail-top [start]
  html = '';
  html += `
    <div id = "studyTitle">${data.studyTitle}</div>`;

  if ((data.studyStatus == 'RECRUIT' || data.studyStatus == 'RECRUIT_COMPLETE')
      && userNickname == data.leader) {
    html += `<a id = "studyUpdate" href="/study/update/${studyId}">수정</a>`
  }

  html += `
    <div class = "status">
      <div class = "people">
          <i id="people-icon" class="bi bi-people-fill"></i>
          <span>인원 : ${data.acceptedStudyMember} / ${data.studyPeople}명</span>
      </div>
   `;

  if (data.studyStatus == 'RECRUIT') {
    html += `<p id = "recruit">모집 중</p>`;
  }else if (data.studyStatus == 'RECRUIT_COMPLETE') {
    html += `<p id = "recruit">모집 완료</p>`;
  }else if (data.studyStatus == 'PROGRESS') {
    html += `<p id = "progress">진행 중</p>`;
  } else {
    html += `<p id = "complete">완료</p>`;
  }
  html += `</div>`;

  studyDetail_top.innerHTML = html;
  // StudyDetail-top [end]
}

function dis(data) {
  console.log("dis 실행");
}

function findTeamMateList(studyId) {
  console.log("findTeamMateList 실행");
  return fetch(`/${studyId}/teamMate/data`)
  .then(response => response.json());
}

function checkTeamMate(teamMateList) {
  isParticipation = false;
  isTeamMate = false;
  isDelete = false;
  cnt = 0;
  console.log("checkTeamMate 실행");
  for(let i = 0; i < teamMateList.length; i++){
    if(teamMateList[i].userNickname == userNickname) {   // 현재 유저가 팀원이라면, isParticipation은 true
      isParticipation = true;
      if(teamMateList[i].teamMateStatus == 'ACCEPTED'){  // 현재 유저가 팀원인 채, 수락된 상황이라면, isTeamMate은 true
        isTeamMate = true;
      }
    }
    if (teamMateList[i].teamMateStatus == 'ACCEPTED') {
      cnt += 1;
    }
  }
  if (cnt == 1) {    // 팀원이 오직 나 하나뿐이라면, 바로 탈퇴가 가능하다.
    isDelete = true;
  }
  console.log("isParticipation : " + isParticipation);
  console.log("isTeamMate : " + isTeamMate);
  console.log("isDelete : " + isDelete);

}

function create() {
  console.log("create 실행");
  if (!confirm('스터디에 참가 신청하시겠습니까?')) {
    return false;
  }
  else {
    fetch(`/${studyId}/teamMate/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then(async () => {
      const teamMateList = await findTeamMateList(studyId, userNickname);
      checkTeamMate(teamMateList);
      findStudyDetail(studyId);
    })
    alert('신청이 완료되었습니다.')
  }
}

function quit() {
  console.log("quit 실행");
  if(!confirm('스터디에서 탈퇴하시겠습니까?')) {
    return false;
  } else {
    fetch(`/${studyId}/teamMate/quit`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then(async () => {
      const teamMateList = await findTeamMateList(studyId, userNickname);
      checkTeamMate(teamMateList);
      findStudyDetail(studyId);
    })
    alert('탈퇴가 완료되었습니다.');
  }
}