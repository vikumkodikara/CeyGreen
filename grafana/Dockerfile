FROM grafana/grafana:10.4.8

USER root
ENV GF_PATHS_PLUGINS=/opt/plugins
RUN mkdir -p /opt/plugins \
    && grafana-cli --pluginsDir /opt/plugins \
         --pluginUrl https://github.com/grafana/grafana-infinity-datasource/releases/download/v2.12.2/yesoreyeram-infinity-datasource-2.12.2.zip \
         plugins install yesoreyeram-infinity-datasource \
    && chown -R grafana:root /opt/plugins

USER grafana
