cert:
	cd src/main/resources/certs && \
	keytool -genkeypair -alias thrift -keyalg RSA -validity 3650 -keystore server.jks && \
	keytool -export -alias thrift -keystore server.jks -rfc -file public.cer && \
	keytool -import -alias thrift -file public.cer  -keystore public.jks
