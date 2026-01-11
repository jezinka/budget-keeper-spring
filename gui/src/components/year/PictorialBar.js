import ReactECharts from "echarts-for-react";
import {useEffect, useState} from "react";

const PictorialBar = ({year}) => {
    const [maxData, setMaxData] = useState(0);
    const [currentData, setCurrentData] = useState(0);

    useEffect(() => {
        loadInvestmentGoalPieData();
    }, [year]);

    async function loadInvestmentGoalPieData() {
        const response = await fetch("/budget/expenses/investmentGoalForYear?year=" + year);
        const data = await response.json();
        setMaxData(Math.max(data.actual, data.target));
        setCurrentData(data.actual);
    }

    const treeDataURI = 'image://data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAA2CAYAAADUOvnEAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA5tJREFUeNrcWE1oE0EUnp0kbWyUpCiNYEpCFSpIMdpLRTD15s2ePHixnj00N4/GoyfTg2fbiwdvvagHC1UQ66GQUIQKKgn1UAqSSFua38b3prPJZDs7s5ufKn0w7CaZ2W/fe9/73kyMRqNB3Nrj1zdn4RJ6du9T2u1a2iHYSxjP4d41oOHGQwAIwSUHIyh8/RA8XeiXh0kLGFoaXiTecw/hoTG4ZCSAaFkY0+BpsZceLtiAoV2FkepZSDk5EpppczBvpuuQCqx0YnkYcVVoqQYMyeCG+lFdaGkXeVOFNu4aEBalOBk6sbQrQF7gSdK5JXjuHXuYVIVyr0TZ0FjKDeCs6km7JYMUdrWAUVmZUBtmRnVPK+x6nIR2xomH06R35ggwJPeofWphr/W5UjPIxq8B2bKgE8C4HVHWvg+2gZjXj19PkdFztY7bk9TDCH/g6oafDPpaoMvZIRI5WyMB/0Hv++HkpTKE0kM+A+h20cPAfN4GuRyp9G+LMTW+z8rCLI8b46XO9zRcYZTde/j0AZm8WGb3Y2F9KLlE2nqYkjFLJAsDOl/lea0q55mqxXcL7YBc++bsCPMe8mUyU2ZIpnCoblca6TZA/ga2Co8PGg7UGUlEDd0ueptglbrRZLLE7poti6pCaWUo2pu1oaYI1CF9b9cCZPO3F8ikJQ/rPpQT5YETht26ss+uCIL2Y8vHwJGpA96GI5mjOlaKhowUy6BcNcgIhDviTGWCGFaqEuufWz4pgcbCh+w0gEOyOjTlTtYYlIWPYWKEsLDzOs+nhzaO1KEpd+MXpOoTUgKiNyhdy5aSMPNVqxtSsJFgza5EWA4zKtCJ2OGbLn0JSLu8+SL4G86p1Fpr7ABXdGFF/UTD4rfmFYFw4G9VAJ9SM3aF8l3yok4/J6IV9sDVb36ynmtJ2M5+CwxTYBdKNMBaocKGV2nYgkz6r+cHBP30MzAfi4Sy+BebSoPIOi8PW1PpCCvr/KOD4k9Zu0WSH0Y0+SxJ2awp/nlwKtcGyHOJ8vNHtRJzhPlsHr8MogtlVtwUU0tSM1x58upSKbfJnSKUR07GVMKkDNfXpzpv0RTHy3nZMVx5IOWdZIaPabGFvfpwpjnvfmJHXLaEvZUTseu/TeLc+xgAPhEAb/PbjO6PBaOTf6LQRh/dERde23zxLtOXbaKNhfq2L/1fAOPHDUhOpIf5485h7l+GNHHiSYPKE3Myz9sFxoJuAyazvwIMAItferha5LTqAAAAAElFTkSuQmCC';

    return <ReactECharts option={{
        tooltip: {},
        xAxis: {
            max: maxData,
            splitLine: {show: false},
            offset: 10,
            axisLine: {
                lineStyle: {
                    color: '#999'
                }
            },
            axisLabel: {
                show: false
            }
        },
        yAxis: {
            data: [year],
            inverse: true,
            axisTick: {show: false},
            axisLine: {show: false},
            axisLabel: {show: false}
        },
        grid: {
            top: 'center',
            height: 50,
            left: 10,
            right: 50
        },
        series: [
            {
                // current data
                type: 'pictorialBar',
                symbol: treeDataURI,
                symbolRepeat: 'fixed',
                symbolMargin: '5%',
                symbolClip: true,
                symbolSize: 20,
                symbolBoundingData: maxData,
                data: [currentData],
                markLine: {
                    symbol: 'none',
                    label: {
                        formatter: '{c}',
                        position: 'start'
                    },
                    lineStyle: {
                        color: 'green',
                        type: 'dotted',
                        opacity: 0.2,
                        width: 2
                    },
                    data: [
                        {
                            type: 'max'
                        }
                    ]
                },
                z: 10
            },
            {
                // full data
                type: 'pictorialBar',
                itemStyle: {
                    opacity: 0.2
                },
                label: {
                    show: true,
                    formatter: function (params) {
                        return ((params.value / maxData) * 100).toFixed(1) + ' %';
                    },
                    position: 'right',
                    offset: [10, 0],
                    color: 'green',
                    fontSize: 18
                },
                symbolRepeat: 'fixed',
                symbolMargin: '5%',
                symbol: treeDataURI,
                symbolSize: 20,
                symbolBoundingData: maxData,
                data: [currentData],
                z: 5
            }
        ]
    }} style={{height: '400px'}}/>
}
export default PictorialBar