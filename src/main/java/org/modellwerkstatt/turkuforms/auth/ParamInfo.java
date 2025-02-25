package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.router.QueryParameters;

import java.util.List;
import java.util.Map;

import static org.modellwerkstatt.turkuforms.auth.NavigationUtil.*;

public class ParamInfo {
    private Map<String, List<String>> params;

    public ParamInfo(QueryParameters queryParameters) {
        params = queryParameters.getParameters();

    }

    public boolean wasActiveLogout(){ return params.containsKey(NavigationUtil.WAS_ACTIVE_LOGOUT_PARAM); }

    public boolean hasCommandToStartLegacy() {
        return params.containsKey(CMD_TO_START);
    }

    public String getCommandToStartLegacy() {
        return params.get(CMD_TO_START).get(0);
    }

    public String getFirstParamLegacy() {
        if (params.containsKey(CMD_TO_START_PARAM)) {
            return params.get(CMD_TO_START_PARAM).get(0);
        }
        return null;
    }

    public String getParamsToForwardIfAny() {
        StringBuilder sb = new StringBuilder();

        if (hasCommandToStartLegacy()) {
            sb.append(CMD_TO_START + "=" + getCommandToStartLegacy());

            if (params.containsKey(CMD_TO_START_PARAM)) {
                sb.append("&" + CMD_TO_START_PARAM + "=" + params.get(CMD_TO_START_PARAM).get(0));
            }

        } else if (hasReroute()) {
            sb.append(REROUTE_TO + "=" + getReroute());

        }

        if (hasUsername()) {
            if (sb.length() > 0) { sb.append("&"); }
            sb.append(USERNAME_PARAM + "=" + getUsername());
        }

        if (sb.length() > 0) {
            return "?" + sb.toString();
        } else {
            return "";
        }
    }

    public boolean hasUsername() { return params.containsKey(USERNAME_PARAM); }
    public String getUsername() { return params.get(USERNAME_PARAM).get(0); }

    public String getOnlyUsernameParam() {
        String params = "&" + USERNAME_PARAM + "=" + getUsername();
        return params;
    }

    public boolean hasReroute() {
        return params.containsKey(REROUTE_TO);
    }

    public String getReroute() {
        String reroute = params.get(REROUTE_TO).get(0);

        if (!reroute.startsWith("/")) { reroute = "/" + reroute; }
        return reroute;
    }

    @Override
    public String toString() {
        return "ParamInfo " + hashCode() + " = " + getParamsToForwardIfAny();
    }
}
