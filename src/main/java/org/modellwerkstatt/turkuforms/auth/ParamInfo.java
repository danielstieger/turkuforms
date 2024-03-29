package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.router.QueryParameters;

import java.util.List;
import java.util.Map;

import static org.modellwerkstatt.turkuforms.auth.NavigationUtil.CMD_TO_START;
import static org.modellwerkstatt.turkuforms.auth.NavigationUtil.CMD_TO_START_PARAM;

public class ParamInfo {
    private Map<String, List<String>> params;

    public ParamInfo(QueryParameters queryParameters) {
        params = queryParameters.getParameters();
    }

    public boolean wasActiveLogout(){ return params.containsKey(NavigationUtil.WAS_ACTIVE_LOGOUT_PARAM); }

    public boolean hasCommandToStart() {
        return params.containsKey(CMD_TO_START);
    }

    public String getCommandToStart() {
        return params.get(CMD_TO_START).get(0);
    }

    public String getFirstParam() {
        if (params.containsKey(CMD_TO_START_PARAM)) {
            return params.get(CMD_TO_START_PARAM).get(0);
        }
        return null;
    }

    public String getParamsToForwardIfAny() {
        StringBuilder sb = new StringBuilder();

        if (hasCommandToStart()) {
            sb.append("?"+ CMD_TO_START + "=" + getCommandToStart());

            if (params.containsKey(CMD_TO_START_PARAM)) {
                sb.append("&" + CMD_TO_START_PARAM + "=" + params.get(CMD_TO_START_PARAM).get(0));
            }
        }

        return sb.toString();
    }
}
