package scraper.connections;

public interface HttpRequestSender {
  ResponseDto sendGET(RequestDto requestDto);

  ResponseDto sendPOST(RequestDto requestDto);
}