document.addEventListener('DOMContentLoaded', () => {
    console.log("Dashboard DOM loaded.");

    // Khai báo các phần tử
    const registrationForm = document.getElementById('registration-form');
    const registrationNumberInput = document.getElementById('registrationNumberInput');
    const registrationNumberError = document.getElementById('registrationNumberError');
    const submitBtn = document.getElementById('submitBtn');
    const detailedScoresContent = document.getElementById('detailedScoresContent');
    const detailedScoresLoading = document.getElementById('detailedScoresLoading');
    const detailedScoresError = document.getElementById('detailedScoresError');
    const dashboardContent = document.getElementById('dashboardContent');
    const dashboardLoading = document.getElementById('dashboardLoading');
    const dashboardError = document.getElementById('dashboardError');
    const reportsContent = document.getElementById('reportsContent');
    const reportsLoading = document.getElementById('reportsLoading');
    const reportsError = document.getElementById('reportsError');
    const navLinks = document.querySelectorAll('.nav-link');
    const sections = document.querySelectorAll('main section');

    // --- Helper Functions ---
    function displayError(errorDiv, message) {
        errorDiv.textContent = message;
        errorDiv.classList.remove('d-none');
    }

    function hideError(errorDiv) {
        errorDiv.textContent = '';
        errorDiv.classList.add('d-none');
    }

    function displayValidationError(inputElement, errorElement, message) {
        inputElement.classList.add('is-invalid');
        if (errorElement) errorElement.textContent = message;
    }

    function clearValidationError(inputElement) {
        inputElement.classList.remove('is-invalid');
    }

    function showSection(sectionId) {
        sections.forEach(section => {
            section.classList.add('d-none');
            if (section.id === sectionId) {
                section.classList.remove('d-none');
            }
        });
        navLinks.forEach(link => {
            link.classList.remove('active', 'fw-bold');
            if (link.dataset.section === sectionId) {
                link.classList.add('active', 'fw-bold');
            }
        });
    }

    // --- Load Dashboard (Top 10 Khối A) ---
    async function loadDashboard() {
        hideError(dashboardError);
        dashboardContent.innerHTML = '<p class="text-muted">Loading ...</p>';
        dashboardLoading.classList.remove('d-none');

        try {
            const response = await fetch('/api/scores/top10');
            if (!response.ok) throw new Error('Không thể tải top 10 điểm khối A');
            const data = await response.json();

            if (data.length === 0) {
                dashboardContent.innerHTML = '<p class="text-muted">Không có dữ liệu top 10.</p>';
                return;
            }

            dashboardContent.innerHTML = `
                <div class="table-responsive">
                    <table class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>SBD</th>
                                <th>Toán</th>
                                <th>Vật Lý</th>
                                <th>Hóa Học</th>
                                <th>Tổng Điểm</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${data.map(student => `
                                <tr>
                                    <td>${student.sbd}</td>
                                    <td>${student.toan || '-'}</td>
                                    <td>${student.vatLi || '-'}</td>
                                    <td>${student.hoaHoc || '-'}</td>
                                    <td>${student.total}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        } catch (error) {
            displayError(dashboardError, `Lỗi: ${error.message}`);
            dashboardContent.innerHTML = '<p class="text-muted">Danh sách top 10 học sinh có điểm khối A cao nhất sẽ hiển thị tại đây.</p>';
        } finally {
            dashboardLoading.classList.add('d-none');
        }
    }

    // --- Load Search Scores ---
    async function searchScores(regNumber) {
        hideError(detailedScoresError);
        detailedScoresContent.innerHTML = '<p class="text-muted">Loading ...</p>';
        detailedScoresLoading.classList.remove('d-none');

        try {
            const response = await fetch(`/api/scores/${regNumber}`);
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error(`SBD ${regNumber} does not exist. Please try again!!!`);
                }
                throw new Error('An error occurred while retrieving scores. Please try again');
            }
            const data = await response.json();
            detailedScoresContent.innerHTML = `
                <p>Kết quả cho SBD: <strong>${regNumber}</strong></p>
                <ul class="list-group list-group-flush">
                    <li class="list-group-item d-flex justify-content-between"><span>Toán:</span> <strong>${data.toan || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Ngữ Văn:</span> <strong>${data.nguVan || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Ngoại Ngữ:</span> <strong>${data.ngoaiNgu || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Mã Ngoại Ngữ:</span> <strong>${data.maNgoaiNgu || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Vật Lý:</span> <strong>${data.vatLi || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Hóa Học:</span> <strong>${data.hoaHoc || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Sinh Học:</span> <strong>${data.sinhHoc || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Lịch Sử:</span> <strong>${data.lichSu || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>Địa Lý:</span> <strong>${data.diaLi || '-'}</strong></li>
                    <li class="list-group-item d-flex justify-content-between"><span>GDCD:</span> <strong>${data.gdcd || '-'}</strong></li>
                </ul>
            `;
        } catch (error) {
            displayError(detailedScoresError, error.message);
            detailedScoresContent.innerHTML = '<p class="text-muted">Detailed view of search scores here.</p>';
        } finally {
            detailedScoresLoading.classList.add('d-none');
        }
    }

    // --- Load Reports (Statistics Chart) ---
    let scoreCharts = {};

    async function loadReports() {
        hideError(reportsError);
        if (!reportsContent) {
            console.error('reportsContent is null');
            displayError(reportsError, 'Không tìm thấy nội dung báo cáo.');
            return;
        }
        reportsContent.innerHTML = '<p class="text-muted">Loading ...</p>';
        reportsLoading.classList.remove('d-none');

        try {
            const response = await fetch('/api/scores/statistics');
            if (!response.ok) throw new Error('Không thể tải dữ liệu thống kê');
            const data = await response.json();

            if (data.length === 0) {
                reportsContent.innerHTML = '<p class="text-muted">Không có dữ liệu thống kê.</p>';
                return;
            }

            // Xóa nội dung cũ và tạo container cho các biểu đồ
            const chartsContainer = document.createElement('div');
            chartsContainer.id = 'chartsContainer';
            chartsContainer.className = 'row row-cols-1 row-cols-md-2 g-4'; // Responsive layout
            reportsContent.innerHTML = '';
            reportsContent.appendChild(chartsContainer);

            data.forEach(dto => {
                const subject = dto.subject;
                const distribution = dto.distribution;

                // Tạo container cho từng môn (col trong Bootstrap)
                const subjectCol = document.createElement('div');
                subjectCol.className = 'col';

                // Nội dung cho từng môn
                subjectCol.innerHTML = `
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h6 class="card-title fw-bold">${subject}</h6>
                            <div class="chart-container">
                                <canvas id="barChart-${subject}"></canvas>
                            </div>
                            <div class="chart-container mt-3">
                                <canvas id="pieChart-${subject}"></canvas>
                            </div>
                        </div>
                    </div>
                `;

                chartsContainer.appendChild(subjectCol);

                // Dữ liệu cho biểu đồ (chỉ lấy 4 loại điểm chính)
                const labels = ['Score <4', 'Score 4-6', 'Score 6-8', 'Score >=8'];
                const dataValues = labels.map(key => distribution[key.replace('Score ', '')] || 0);
                const total = dataValues.reduce((a, b) => a + b, 0);

                // Biểu đồ cột
                if (scoreCharts[`bar-${subject}`]) scoreCharts[`bar-${subject}`].destroy();
                scoreCharts[`bar-${subject}`] = new Chart(document.getElementById(`barChart-${subject}`), {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Number Of Students',
                            data: dataValues,
                            backgroundColor: 'rgba(75, 192, 192, 0.7)',
                            borderColor: 'rgba(75, 192, 192, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            title: { display: true, font: { size: 14 } },
                            legend: { display: false }
                        },
                        scales: {
                            x: {
                                title: { display: true, text: 'Score Level', font: { size: 12 } },
                                ticks: { font: { size: 10 } }
                            },
                            y: {
                                beginAtZero: true,
                                title: { display: true, text: 'Number Of Students', font: { size: 12 } },
                                ticks: {
                                    stepSize: 1,
                                    font: { size: 10 },
                                    callback: function(value) { return Number.isInteger(value) ? value : null; }
                                }
                            }
                        }
                    }
                });

                // Biểu đồ tròn
                const pieData = labels.map((label, index) => ({
                    label: label,
                    data: distribution[label.replace('Score ', '')] || 0,
                    percentage: total > 0 ? ((distribution[label.replace('Score ', '')] || 0) / total * 100).toFixed(2) + '%' : '0%'
                }));
                if (scoreCharts[`pie-${subject}`]) scoreCharts[`pie-${subject}`].destroy();
                scoreCharts[`pie-${subject}`] = new Chart(document.getElementById(`pieChart-${subject}`), {
                    type: 'pie',
                    data: {
                        labels: pieData.map(d => `${d.label} (PER: ${d.percentage}, QTY: ${d.data})`),
                        datasets: [{
                            data: pieData.map(d => d.data),
                            backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0']
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            title: { display: true, font: { size: 14 } },
                            legend: {
                                position: 'bottom',
                                labels: {
                                    boxWidth: 10,
                                    font: { size: 10 },
                                    padding: 10
                                }
                            }
                        }
                    }
                });
            });
        } catch (error) {
            displayError(reportsError, `Lỗi: ${error.message}`);
            reportsContent.innerHTML = '<div id="chartsContainer"></div>';
        } finally {
            reportsLoading.classList.add('d-none');
        }
    }

    // --- Event Listeners ---
    if (registrationNumberInput) {
        // Chặn nhập ký tự không phải số
        registrationNumberInput.addEventListener('keypress', (event) => {
            const charCode = event.charCode;
            if (charCode < 48 || charCode > 57) { // 48-57 là mã ASCII của số 0-9
                event.preventDefault();
            }
        });

        // Xóa các ký tự không phải số nếu người dùng dán giá trị
        registrationNumberInput.addEventListener('input', (event) => {
            const value = event.target.value;
            event.target.value = value.replace(/[^0-9]/g, '');
        });
    }
    if (registrationForm) {
        registrationForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            clearValidationError(registrationNumberInput);
            hideError(detailedScoresError);
            detailedScoresContent.innerHTML = '';
            detailedScoresLoading.classList.remove('d-none');
            submitBtn.disabled = true;

            const regNumber = registrationNumberInput.value.trim();

            // Bỏ qua validation nếu muốn kiểm tra ngay cả khi không hợp lệ
            // if (!/^\d{7,12}$/.test(regNumber)) {
            //     displayValidationError(registrationNumberInput, registrationNumberError, 'Số báo danh phải có 7-12 chữ số.');
            //     detailedScoresLoading.classList.add('d-none');
            //     submitBtn.disabled = false;
            //     return;
            // }

            await searchScores(regNumber);
            submitBtn.disabled = false;
        });
    }

    navLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            const sectionId = link.dataset.section;
            showSection(sectionId);

            if (sectionId === 'dashboard') {
                loadDashboard();
            } 
            else if (sectionId === 'reports') {
                loadReports();
            }
            else if (sectionId === 'search-scores') {
                registrationForm.reset();
                clearValidationError(registrationNumberInput);
                hideError(detailedScoresError);
                detailedScoresContent.innerHTML = '<p class="text-muted">Detailed view of search scores here.</p>';
            }
        });
    });

    // --- Initial Setup ---
    showSection('dashboard');
    loadDashboard();
});
