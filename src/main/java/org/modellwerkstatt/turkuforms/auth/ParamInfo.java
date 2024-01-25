package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.router.QueryParameters;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;

import java.util.List;
import java.util.Map;

public class ParamInfo {
    private Map<String, List<String>> params;

    public ParamInfo(QueryParameters queryParameters) {
        params = queryParameters.getParameters();
    }

    public boolean wasActiveLogout(){ return params.containsKey(NavigationUtil.WAS_ACTIVE_LOGOUT_PARAM); }

    public boolean hasCommandToStart() {
        return params.containsKey("command");
    }

    public String getCommandToStart() {
        return params.get("command").get(0);
    }

    public String getFirstParam() {
        if (params.containsKey("param")) {
            return params.get("param").get(0);
        }
        return null;
    }

    public String getParamsToForwardIfAny() {
        StringBuilder sb = new StringBuilder();

        if (hasCommandToStart()) {
            sb.append("?command=" + getCommandToStart());

            if (params.containsKey("param")) {
                sb.append("&param=" + params.get("param").get(0));
            }
        }

        return sb.toString();
    }
}
