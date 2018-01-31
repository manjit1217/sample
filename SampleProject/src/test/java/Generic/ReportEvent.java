package Generic;


	public class ReportEvent extends BaseClass {

		private String result;
		private String resultText;
		private String screenShotpath;

		public ReportEvent(String result, String resultText) {
			this.result = result;
			this.resultText = resultText;
		}

		public ReportEvent(String result, String resultText, String screenShotpath) {
			this.result = result;
			this.resultText = resultText;
			this.screenShotpath = screenShotpath;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public void setResultText(String resultText) {
			this.resultText = resultText;
		}

		public void setscreenShotpath(String screenShotpath) {
			this.screenShotpath = screenShotpath;
		}

		public String getResult() {
			return result;
		}

		public String getResultText() {
			return resultText;
		}

		public String getscreenShotpath() {
			return screenShotpath;
		}
	}
