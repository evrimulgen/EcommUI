package com.envista.msi.api.web.rest.dto.invoicing;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by KRISHNAREDDYM on 4/12/2017.
 */
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "VoiceDto.getVoiceList", procedureName = "shp_inv_get_voice_list",
        resultClasses = VoiceDto.class,
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_USER_ID", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_ID", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_PARENT_VOICE_ID", type = Long.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_ACTION_TYPE", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "P_REFCUR_VOICE_INFO", type = Void.class)
        }),
        @NamedStoredProcedureQuery(name = "VoiceDto.createOrUpdateVoice", procedureName = "SHP_INV_INSERT_OR_UPDATE_VOICE",
                resultClasses = VoiceDto.class,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_ID", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_NAME", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_TYPE", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_ALIAS_FOR", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_PARENT_VOICE_ID", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_COMMENTS", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_IS_ACTIVE", type = Boolean.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_USER_ID", type = Long.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_ACTION_TYPE", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "P_REFCUR_VOICE_INFO", type = Void.class)
        }),
        @NamedStoredProcedureQuery(name = "VoiceDto.searchVoice", procedureName = "SHP_INV_SEARCH_VOICE",
                resultClasses = VoiceDto.class,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_NAMES", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_TYPE", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_FLAG", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_PARENT_VOICE_NAMES", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_COMMENTS", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "P_REFCUR_VOICE_INFO", type = Void.class)
        }),
        @NamedStoredProcedureQuery(name = "VoiceDto.findByVoiceName", procedureName = "SHP_INV_GET_BY_VOICE_NAME",
                resultClasses = VoiceDto.class,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_NAME", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_PREV_VOICE_NAME", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "P_REFCUR_VOICE_INFO", type = Void.class)
        }),
        @NamedStoredProcedureQuery(name = "VoiceDto.deleteVoice", procedureName = "SHP_INV_DELETE_VOICE",
                resultClasses = VoiceDto.class,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_VOICE_IDS", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "P_REFCUR_VOICE_INFO", type = Void.class)
                })
})
/*@SqlResultSetMappings({
        @SqlResultSetMapping(name = "deleteCount", classes = {
                @ConstructorResult(
                        targetClass = VoiceDto.class,
                        columns = {
                                @ColumnResult(name = "DELETE_COUNT", type = int.class)
                        })
        })
})*/
@Entity
public class VoiceDto implements Serializable {

    @Id
    @Column(name = "VOICE_ID")
    private Long voiceId;

    @Column(name = "VOICE_NAME", nullable = false, unique = true)
    private String voiceName;

    @Column(name = "VOICE_TYPE", nullable = true)
    private String voiceType;

    @Column(name = "ALIAS_FOR", nullable = true)
    private String aliasFor;

    @Column(name = "PARENT_VOICE_ID")
    private Long parentVoiceId;

    @Column(name = "COMMENTS", nullable = true)
    private String comments;

    @Column(name = "IS_ACTIVE")
    private boolean active;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "PARENT_VOICE_NAME", nullable = false, unique = true)
    private String parentVoiceName;

/*    @Column(name = "DELETE_COUNT")
    private int deleteCount;*/

    public VoiceDto() {
    }

/*    public VoiceDto(int deleteCount) {
        this.deleteCount = deleteCount;
    }*/

    public VoiceDto(Long voiceId, String voiceName, String voiceType, String aliasFor) {
        this.voiceId = voiceId;
        this.voiceName = voiceName;
        this.voiceType = voiceType;
        this.aliasFor = aliasFor;
    }

    public Long getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(Long voiceId) {
        this.voiceId = voiceId;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public String getVoiceType() {
        return voiceType;
    }

    public void setVoiceType(String voiceType) {
        this.voiceType = voiceType;
    }

    public String getAliasFor() {
        return aliasFor;
    }

    public void setAliasFor(String aliasFor) {
        this.aliasFor = aliasFor;
    }

    public Long getParentVoiceId() {
        return parentVoiceId;
    }

    public void setParentVoiceId(Long parentVoiceId) {
        this.parentVoiceId = parentVoiceId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getParentVoiceName() {
        return parentVoiceName;
    }

    public void setParentVoiceName(String parentVoiceName) {
        this.parentVoiceName = parentVoiceName;
    }

    /*    public int getDeleteCount() {
        return deleteCount;
    }

    public void setDeleteCount(int deleteCount) {
        this.deleteCount = deleteCount;
    }*/
}
