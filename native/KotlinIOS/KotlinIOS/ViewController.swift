import UIKit
import SharedCode

class ViewController: UIViewController {
    @IBOutlet weak var originSpinner: UIPickerView!
    @IBOutlet weak var destinationSpinner: UIPickerView!
    @IBOutlet weak var submitButton: UIButton!
    @IBOutlet weak var departureTable: UITableView!
    
    private var stations: [String] = [String]()
    private var departures: [DepartureInformation] = [DepartureInformation]()
    
    private let presenter: ApplicationContractPresenter = ApplicationPresenter()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        presenter.onViewTaken(view: self)

        departureTable.delegate = self
        departureTable.dataSource = self
        self.registerTableViewCells()

        setUpPickers()
    }

    @IBAction func stationSubmitButtonTapped(_ sender: Any) {
        let originStation = stations[originSpinner.selectedRow(inComponent: 0)]
        let destinationStation = stations[destinationSpinner.selectedRow(inComponent: 0)]
        presenter.onStationSubmitButtonPressed(originStation: originStation, destinationStation: destinationStation)
    }
}

extension ViewController: UIPickerViewDataSource, UIPickerViewDelegate {
    func setUpPickers() {
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

extension ViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return departures.capacity
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "DepartureTableViewCell") as? DepartureTableViewCell {
            cell.departureTime.text = departures[indexPath.row].departureTime
            cell.arrivalTime.text = departures[indexPath.row].arrivalTime
            cell.price.text = departures[indexPath.row].price
            cell.travelTime.text = departures[indexPath.row].journeyTime
            cell.primaryOperator.text = departures[indexPath.row].trainOperator
            cell.purchase.setTitle("Buy", for: .normal)
            return cell
        }
        return DepartureTableViewCell()
    }

    private func registerTableViewCells() {
        let departureCell = UINib(nibName: "DepartureTableViewCell",
                                  bundle: nil)
        self.departureTable.register(departureCell,
                                forCellReuseIdentifier: "DepartureTableViewCell")
    }
}

extension ViewController: ApplicationContractView {
    func setDepartureTable(departures: [DepartureInformation]) {
        self.departures = departures
        departureTable.reloadData()
    }

    func openUrl(url: String) {
        UIApplication.shared.open(URL(string: url)!)
    }

    func setStationSubmitButtonText(text: String) {
        submitButton.setTitle(text, for: .normal)
    }
    
    func populateOriginAndDestinationSpinners(stations: Array<String>) {
        self.stations = stations
    }
}
