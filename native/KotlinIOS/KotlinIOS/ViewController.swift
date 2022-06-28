import UIKit
import SharedCode

class ViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {
    @IBOutlet weak var originSpinner: UIPickerView!
    @IBOutlet weak var destinationSpinner: UIPickerView!
    @IBOutlet weak var submitButton: UIButton!
    
    private var stations: [String] = [String]()
    
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)
        
        originSpinner.delegate = self
        originSpinner.dataSource = self
        
        destinationSpinner.delegate = self
        destinationSpinner.dataSource = self
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return stations.capacity
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return stations[row]
    }
    

}

extension ViewController: ApplicationContractView {
    func getDestinationStation() -> String {
        let row = destinationSpinner.selectedRow(inComponent: 0)
        return stations[row]
    }
    
    func getOriginStation() -> String {
        let row = originSpinner.selectedRow(inComponent: 0)
        return stations[row]
    }
    
    func openUrl(url: String) {
        UIApplication.shared.open(URL(string: url)!)
        return
    }
    
    func setStationSubmitButtonHandler() {
        submitButton.addTarget(self, action: #selector(buttonPress(_:)), for: UIControl.Event.touchDown)
    }
    
    @objc func buttonPress(_ sender: UIButton){
        // TODO: better method to do this?
        presenter.onStationSubmitButtonPressed()
    }
    
    
    func setStationSubmitButtonText(text: String) {
        submitButton.setTitle(text, for: .normal)
    }
    
    func populateOriginAndDestinationSpinners(stations: Array<String>) {
        self.stations = stations
    }
}
