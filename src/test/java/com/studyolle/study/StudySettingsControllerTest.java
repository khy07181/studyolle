package com.studyolle.study;

import com.studyolle.WithAccount;
import com.studyolle.domain.Account;
import com.studyolle.domain.Study;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class StudySettingsControllerTest extends StudyControllerTest {

    @Test
    @WithAccount("hayoung")
    @DisplayName("스터디 소개 수정 폼 조회 - 실패 (권한 없는 유저)")
    void updateDescriptionForm_fail() throws Exception {
        Account kimhayoung = createAccount("kimhayoung");
        Study study = createStudy("test-study", kimhayoung);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAccount("hayoung")
    @DisplayName("스터디 소개 수정 폼 조회 - 성공")
    void updateDescriptionForm_success() throws Exception {
        Account hayoung = accountRepository.findByNickname("hayoung");
        Study study = createStudy("test-study", hayoung);

        mockMvc.perform(get("/study/" + study.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/settings/description"))
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"));
    }

    @Test
    @WithAccount("hayoung")
    @DisplayName("스터디 소개 수정 - 성공")
    void updateDescription_success() throws Exception {
        Account hayoung = accountRepository.findByNickname("hayoung");
        Study study = createStudy("test-study", hayoung);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionUrl))
                .andExpect(flash().attributeExists("message"));
    }

    @Test
    @WithAccount("hayoung")
    @DisplayName("스터디 소개 수정 - 실패")
    void updateDescription_fail() throws Exception {
        Account hayoung = accountRepository.findByNickname("hayoung");
        Study study = createStudy("test-study", hayoung);

        String settingsDescriptionUrl = "/study/" + study.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionUrl)
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("studyDescriptionForm"))
                .andExpect(model().attributeExists("study"))
                .andExpect(model().attributeExists("account"));
    }

}